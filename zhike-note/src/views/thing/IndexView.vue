<template>
    <!-- 小记页面 -->
    <n-layout embedded content-style="padding:24px">
        <!-- 小记列表页的标头 -->
        <n-card size="small" :bordered="false">
            <!-- 小记页面标题 -->
            <template #header>
                <h3>小记列表</h3>
            </template>
            <!-- 新增小记按钮 -->
            <template #header-extra>
                <n-space>
                    <!-- 搜索输入框 -->
                    <n-input-group>
                        <n-input v-model:value="search" placeholder="搜索"></n-input>
                        <n-button @click="getThingList(false)">搜索</n-button>
                    </n-input-group>
                    <!-- 过滤选项 -->
                    <n-select @update:value="getThingList(false)" v-model:value="filter" :options="filterOptions"
                        placeholder="过滤" style="width: 130px;">
                    </n-select>
                    <!-- 新增小记按钮 -->
                    <n-button dashed @click="editThingModalRef.showEditModal(null)">新增小记</n-button>
                </n-space>
            </template>
        </n-card>
        <!-- 小记列表容器 -->
        <n-card size="small" :bordered="false" style="margin-top: 20px;">
            <!-- 小记列表骨架屏 -->
            <n-space v-if="loading">
                <n-card size="small" embedded :bordered="isDarkTheme" :segmented="{ 'content': 'soft' }" v-for="n in 9">
                    <template #header>
                        <n-skeleton :width="180" size="small"></n-skeleton>
                    </template>
                    <template #header-extra>
                        <n-skeleton :width="20" repeat="3" circle style="margin-left: 6px;"></n-skeleton>
                    </template>
                    <template #default>
                        <n-space>
                            <n-skeleton :width="50" :height="22"></n-skeleton>
                            <n-skeleton :width="80" :height="22"></n-skeleton>
                            <n-skeleton :width="50" :height="22"></n-skeleton>
                        </n-space>
                    </template>
                    <template #footer>
                        <n-skeleton text :width="140"></n-skeleton>
                    </template>
                </n-card>
            </n-space>
            <!-- 小记列表 -->
            <n-space :wrap-item="false">
                <TransitionGroup @before-enter="beforeEnter" @enter="enterEvent" @before-leave="beforeLeave"
                    @leave="LeaveEvent" move-class="move-transition">
                    <template v-if="!loading && things.length > 0">
                        <ThingCard v-for="(thing, index) in things" :key="thing.id" :id="thing.id" :title="thing.title"
                            :finished="!!thing.finished" :data-index="index" :top="!!thing.top"
                            :tags="thing.tags.split(',')" :time="thing.updateTime" @changeStatus="getThingList()"
                            @delete="showDeleteRemindDialog" @edit="editThingModalRef.showEditModal(thing.id)" />
                    </template>

                </TransitionGroup>

            </n-space>
            <!-- 暂无小记列表的描述 -->
            <n-empty v-if="!loading && things.length === 0" style="margin:20px auto;" size="huge" description="暂无小记列表，快快创建">
                <template #icon>
                    <n-icon :component="SubtitlesOffOutlined"></n-icon>
                </template>
                <template #extra>
                    <n-button dashed @click="editThingModalRef.showEditModal(null)">创建小记</n-button>
                </template>
            </n-empty>

        </n-card>
    </n-layout>
    <!-- 删除提醒框 -->
    <DeleteRemindDialog :title="deleteRemind.title" :show="deleteRemind.show" @delete="toDeleteThing"
        @cancel="deleteRemind.show = false">
    </DeleteRemindDialog>
    <!-- 编辑小记窗口 -->
    <EditThingModal ref="editThingModalRef" @save="getThingList"></EditThingModal>
</template>

<script setup>
import { SubtitlesOffOutlined } from '@vicons/material'
import { useThemeStore } from "@/stores/themeStore"
import { storeToRefs } from "pinia"
import { useUserStore } from '@/stores/userStore'
import { ref, watch } from "vue"
import { getUserToken, loginInvalid } from "@/utils/UserLoginUtil"
import { useMessage, useLoadingBar } from 'naive-ui'
import { noteBaseRequest } from "@/request/note_request"
import ThingCard from "@/components/thing/ThingCard.vue"
import gsap from 'gsap'
import DeleteRemindDialog from '../../components/remind/DeleteRemindDialog.vue'
import EditThingModal from "@/components/thing/EditThingModal.vue"
//消息对象
const message = useMessage()
//加载条对象
const loadingBar = useLoadingBar()
//用户的共享资源
const userStore = useUserStore()
const { token, id: user_id } = storeToRefs(userStore)

watch(
    () => token.value,
    newData => {
        //是否重新进行登录
        if (newData.value !== null) {
            loading.value = true//处于加载状态
            //重新获取用户的小记列表
            getThingList()
            //判断编辑小记的窗口是否需要关闭
            //如果当前是编辑小记而不是新增小记，并且用户id跟当前小记的用户id不一致，关闭编辑小记模态框
            if (editThingModalRef.value.thingId !== null && editThingModalRef.value.userId !== user_id) {
                editThingModalRef.value.show = false//关闭编辑小记模态框
            }
        }
    }
)

//主题store对象
const themeStore = useThemeStore()
const { isDarkTheme } = storeToRefs(themeStore)//是否为暗系主题

//是否处于加载中
const loading = ref(true)
//小记列表
const things = ref([])
//是否是新增小记
const isNewCreate = ref(false)
//获取小记列表
/**
 * 
 * @param {Boolean} newCreate 是否是新增小记
 */
const getThingList = async (newCreate) => {
    isNewCreate.value = newCreate
    //判断用户的登录状态
    const userToken = await getUserToken()

    loadingBar.start()//加载条开始
    //发送获取小记列表请求
    const { data: responseData } = await noteBaseRequest.get(
        "/thing/list",
        {
            params: {
                search: search.value,
                filter: filter.value
            },
            headers: { userToken }
        }
    ).catch(() => {
        loadingBar.error()//加载条异常结束
        //发送请求失败（404，500,400...）
        throw message.error("获取小记列表请求失败")//显示发送请求失败的通知
    })
    //得到服务器返回的数据，进行处理
    console.log(responseData)

    if (responseData.success) {
        loadingBar.finish()//加载条结束
        console.log(responseData.data)
        things.value = responseData.data//小记列表
        loading.value = false;//不处于加载状态
    }
    else {
        loadingBar.error()//加载条异常结束
        message.error(responseData.message)//显示发送请求失败的通知
        if (responseData.code === "L_008") {
            loginInvalid(true)//登录失效
        }
    }
}
getThingList()
/**
 * 置顶小记
 * @param {Boolean} isTop 是否置顶
 * @param {Number} thingId 小记编号
 */
const topThing = async (isTop, thingId) => {
    //判断用户的登录状态
    const userToken = await getUserToken()

    loadingBar.start()//加载条开始
    //发送置顶小记请求
    const { data: responseData } = await noteBaseRequest.get(
        "/thing/top",
        {
            params: { isTop, thingId },
            headers: { userToken }
        }
    ).catch(() => {
        loadingBar.error()//加载条异常结束
        //发送请求失败（404，500,400...）
        throw message.error(isTop ? "置顶小记请求失败" : "取消置顶小记请求失败")//显示发送请求失败的通知
    })
    //得到服务器返回的数据，进行处理
    console.log(responseData)

    if (responseData.success) {
        loadingBar.finish()//加载条结束
        message.success(responseData.message)//显示发送请求成功的通知
        getThingList(false)//置顶后重新获取小记列表
    }
    else {
        loadingBar.error()//加载条异常结束
        message.error(responseData.message)//显示发送请求失败的通知
        if (responseData.code === "L_008") {
            loginInvalid(true)//登录失效
        }
    }
}
//执行显示动画之前的初始位置
const beforeEnter = (el) => {
    gsap.set(el, {
        y: 30,
        opacity: 0
    })
}
//执行显示动画
const enterEvent = (el, done) => {
    gsap.to(el, {
        y: 0,//偏移量
        opacity: 1,//透明度
        duration: 0.5,//单位是秒
        delay: () => (isNewCreate.value ? 0 : el.dataset.index * 0.12),//延迟动画，依次出现
        onComplete: done//动画执行后调用的函数
    })
}
//执行隐藏动画之前的初始位置
const beforeLeave = (el) => {
    gsap.set(el, {
        opacity: 1,
        scale: 1, //缩放量
        position: 'fixed',
        top: 0,
        left: '50%'
    })
}
//执行隐藏动画
const LeaveEvent = (el, done) => {
    gsap.to(el, {
        scale: 0.01,
        opacity: 0,//透明度
        duration: 0.5,//单位是秒
        onComplete: done//动画执行后调用的函数
    })
}
//删除提醒框的对象
const deleteRemind = ref({
    show: false,//是否显示
    id: null,//小记编号
    title: null,//删除单个文件的名称
})
//显示删除小记提醒框
const showDeleteRemindDialog = ({ id, title }) => {
    deleteRemind.value.id = id,//将要删除的小记编号
        deleteRemind.value.title = title//删除小记的标题 
    deleteRemind.value.show = true
}
/**
 * 删除小记
 * @param {*} complete 是否彻底删除 
 */
const toDeleteThing = async (complete) => {
    deleteRemind.value.show = false//关闭提醒框
    //判断用户的登录状态
    const userToken = await getUserToken()
    loadingBar.start()//加载条开始

    //发送删除小记请求
    const { data: responseData } = await noteBaseRequest.delete(
        "/thing/delete",
        {
            params: {
                complete, thingId: deleteRemind.value.id,
                isRecycleBin: false
            },
            headers: { userToken }
        }
    ).catch(() => {
        loadingBar.error()//加载条异常结束
        //发送请求失败（404，500,400...）
        throw message.error(complete ? "彻底删除小记请求失败" : "删除小记请求失败")//显示发送请求失败的通知
    })
    //得到服务器返回的数据，进行处理
    console.log(responseData)

    if (responseData.success) {
        loadingBar.finish()//加载条结束
        message.success(responseData.message)//显示发送请求成功的通知
        getThingList()//重新获取小记列表
    }
    else {
        loadingBar.error()//加载条异常结束
        message.error(responseData.message)//显示发送请求失败的通知
        if (responseData.code === "L_008") {
            loginInvalid(true)//登录失效
        }
    }
}
//编辑小记模态框组件的引用
const editThingModalRef = ref(null)

//搜索关键字
const search = ref(null)
//过滤选择器的值
const filter = ref(null)
//过滤选项
const filterOptions = [
    {
        label: '默认',
        value: null
    },
    {
        label: '未完成',
        value: 0
    },
    {
        label: '已完成',
        value: 1
    }
]
</script>
<style scoped>
.move-transition {
    /* 取消置顶和置顶时有动画 */
    transition: all 0.5s;
}
</style>

