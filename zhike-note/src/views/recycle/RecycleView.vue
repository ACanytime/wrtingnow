<template>
    <div class="page">
        <div class="header">
            <div class="title">回收站</div>
        </div>
        <div class="content">
            <div class="note-list">
                <div class="note" v-for="(note, index) in notes" :key="index">
                    <n-card>
                        <div class="note-title">
                            {{ note.id }}
                            {{ note.title }}</div>
                        <n-ellipsis :tooltip="false" line-clamp="2">
                            <div class="note-title">{{ htmlToText(note.content) }}</div>
                        </n-ellipsis>

                        <div class="note-date">{{ note.time }}</div>
                        <div class="note-actions">
                            <n-button>恢复</n-button>
                            <n-button @click="setContextMenuId(note.id),contextMenu.show=true">彻底删除</n-button>
                            <DeleteRemindDialog :delete-btn="false" :title="note.title" :show="contextMenu.show"
                                @delete="toDeleteThing" @cancel="contextMenu.show = false">
                            </DeleteRemindDialog>
                        </div>
                    </n-card>
                </div>

            </div>
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

const notes = ref([])
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
const getNoteThing = async () => {

    //判断用户的登录状态
    const userToken = await getUserToken()

    loadingBar.start()//加载条开始
    //发送获取笔记列表请求
    const { data: responseData } = await noteBaseRequest.get(
        "/note/recycle",
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
        notes.value = responseData.data
        console.log(notes)
    }
    else {
        loadingBar.error()//加载条异常结束
        message.error(responseData.message)//显示发送请求失败的通知
        if (responseData.code === "L_008") {
            loginInvalid(true)//登录失效
        }
    }
}
getNoteThing()
const toDeleteThing = async (complete) => {
    displaydelete.value = false//关闭提醒框
    //判断用户的登录状态
    const userToken = await getUserToken()
    loadingBar.start()//加载条开始

    //发送删除小记请求
    const { data: responseData } = await noteBaseRequest.delete(
        "/note/deleterecycle",
        {
            params: {
                complete, noteId: contextMenu.value.id,
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
        contextMenu.value.show=false
        getNoteThing()//重新获取小记列表
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
.note-list {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
}

.note {
    width: calc(33.33% - 10px);
    margin-bottom: 20px;
    padding: 10px;
    border: 1px solid #ddd;
    box-sizing: border-box;
}

.note-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 10px;
}

.note-date {
    color: #999;
    margin-bottom: 10px;
}

.note-actions button {
    background-color: gray;
    margin-right: 10px;
}
</style>
  