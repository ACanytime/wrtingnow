<template>
    <div class="page">
        <div class="header">
            <n-tag type="success">
                小记回收站
            </n-tag>
        </div>

        <div class="content">
            <space>
                <div class="thing-list">
                    <TransitionGroup @before-enter="beforeEnter" @enter="enterEvent" @before-leave="beforeLeave"
                        @leave="LeaveEvent" move-class="move-transition">
                        <div class="thing" v-for="(thing, index) in things" :key="index">
                            <n-space>
                                <div class="thing-title">
                                    {{ thing.id }}
                                    {{ thing.title }}
                                </div>
                                <n-ellipsis :tooltip="false" line-clamp="2">
                                    <div class="thing-title">{{ htmlToText(thing.tags) }}</div>
                                </n-ellipsis>

                                <div class="thing-date">{{ thing.time }}</div>
                                <div class="thing-actions">
                                    <n-button>恢复</n-button>
                                    <n-button @click="setContextMenuId(thing.id), contextMenu.show = true">彻底删除</n-button>
                                    <DeleteRemindDialog :delete-btn="false" :title="thing.title" :show="contextMenu.show"
                                        @delete="toDeleteThing(true)" @cancel="contextMenu.show = false">
                                    </DeleteRemindDialog>
                                </div>
                            </n-space>
                        </div>
                    </TransitionGroup>

                </div>
            </space>
        </div>
    </div>
</template>
  

<script setup>
import { ref } from "vue"
import { getUserToken, loginInvalid } from "@/utils/UserLoginUtil"
import { useMessage, useLoadingBar } from 'naive-ui'
import { noteBaseRequest } from "@/request/note_request"
//安装htmlToText包，可以删除文本中的html元素
import { htmlToText } from 'html-to-text'
import DeleteRemindDialog from '../../components/remind/DeleteRemindDialog.vue'
import gsap from 'gsap'

const things = ref([])
//消息对象
const message = useMessage()
//加载条对象
const loadingBar = useLoadingBar()

const contextMenu = ref({
    show: false,
    id: null
})
const setContextMenuId = (id) => {
    contextMenu.value.id = id
}
console.log(contextMenu)
//是否显示删除提醒框
const displaydelete = ref(false)
//是否显示需要延迟动画
let enterDelay = true
//隐藏是否需要动画
let hiddenAnimation = true

//执行显示动画之前的初始位置
const beforeEnter = (el) => {
    gsap.set(el, {
        x: 30,
        opacity: 0
    })
}
//执行显示动画
const enterEvent = (el, done) => {
    gsap.to(el, {
        x: 0,//偏移量
        opacity: 1,//透明度
        duration: 0.5,//单位是秒
        delay: () => (enterDelay.value ? 0 : el.dataset.index * 0.12),//延迟动画，依次出现
        onComplete: done//动画执行后调用的函数
    })
}
//执行隐藏动画之前的初始位置
const beforeLeave = (el) => {
    if (hiddenAnimation) {
        //获取删除的元素距离父组件的左和上位置
        const left = el.offsetLeft
        const top = el.offsetTop
        //设置删除组件的属性(需要脱离文档里)
        gsap.set(el, {
            boxShadow: '0 0 5px black',
            zIndex: 1,
            position: 'absolute',
            top,
            left,
            backdropFilter: 'blur(5px)',
            width: 'calc(100%-24px)'
        })
    }
}
//执行隐藏动画
const LeaveEvent = (el, done) => {
    if (hiddenAnimation) {
        gsap.to(el, {
            scale: 0,//缩放
            duration: 0.5,//动画时间（秒）
            onComplete: done //动画执行完毕后调用的函数
        })
    }
    else {
        gsap.to(el, {
            duration: 0,//单位是秒
            onComplete: done//动画执行后调用的函数
        })
    }
}
const getRecycleThing = async (ed, ha) => {
    enterDelay = ed//显示是否需要延迟动画
    hiddenAnimation = ha //隐藏是否需要动画
    //判断用户的登录状态
    const userToken = await getUserToken()

    loadingBar.start()//加载条开始
    //发送获取笔记列表请求
    const { data: responseData } = await noteBaseRequest.get(
        "/thing/recycle",
        {
            headers: { userToken }
        }
    ).catch(() => {
        loadingBar.error()//加载条异常结束
        //发送请求失败（404，500,400...）
        throw message.error("获取笔记列表请求失败")//显示发送请求失败的通知
    })
    //得到服务器返回的数据，进行处理
    console.log(responseData)

    if (responseData.success) {
        //封装笔记列表
        loadingBar.finish()//加载条结束
        things.value = responseData.data
        console.log(things)
    }
    else {
        loadingBar.error()//加载条异常结束
        message.error(responseData.message)//显示发送请求失败的通知
        if (responseData.code === "L_008") {
            loginInvalid(true)//登录失效
        }
    }
}
getRecycleThing(true, true)
const toDeleteThing = async (complete) => {
    displaydelete.value = false//关闭提醒框
    //判断用户的登录状态
    const userToken = await getUserToken()
    loadingBar.start()//加载条开始

    //发送删除小记请求
    const { data: responseData } = await noteBaseRequest.delete(
        "/thing/deleterecycle",
        {
            params: {
                complete, thingId: contextMenu.value.id,
                isRecycleBin: false
            },
            headers: { userToken }
        }
    ).catch(() => {
        loadingBar.error()//加载条异常结束
        //发送请求失败（404，500,400...）
        throw message.error(complete ? "彻底删除请求失败" : "删除请求失败")//显示发送请求失败的通知
    })
    //得到服务器返回的数据，进行处理
    console.log(responseData)

    if (responseData.success) {
        loadingBar.finish()//加载条结束
        message.success(responseData.message)//显示发送请求成功的通知
        contextMenu.value.show = false
        getRecycleThing(true, true)//重新获取小记列表
    }
    else {
        loadingBar.error()//加载条异常结束
        message.error(responseData.message)//显示发送请求失败的通知
        if (responseData.code === "L_008") {
            loginInvalid(true)//登录失效
        }
    }
}
</script>
<style>
.thing-list {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    /* 添加间距 */
}

.thing {
    width: calc(33% - 10px);
    /* 调整宽度并考虑间距 */
    padding: 20px;
    /* 调整内边距 */
    border: 5px solid #ddd;
    box-sizing: border-box;
}

.thing-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 10px;
    white-space: nowrap;
    /* 不换行 */
    overflow: hidden;
    /* 溢出隐藏 */
    text-overflow: ellipsis;
    /* 超出部分显示省略号 */
}

.thing-date {
    color: #999;
    margin-bottom: 10px;
}

.thing-actions button {
    background-color: gray;
    margin-right: 10px;
    color: white;
    /* 设置文字颜色 */
    border: none;
    /* 去除边框 */
    padding: 8px 16px;
    /* 调整内边距 */
    cursor: pointer;
    /* 鼠标指针样式 */
}

.thing-actions button:last-child {
    margin-right: 0;
    /* 最后一个按钮去除右边距 */
}
</style>

  