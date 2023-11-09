<template>
    <!-- 骨架屏 -->
    <n-space vertical v-if="loading">
        <n-skeleton :height="36" width="100%"></n-skeleton>
        <n-skeleton text width="30%"></n-skeleton>
        <n-skeleton text width="40%"></n-skeleton>
        <n-skeleton text width="80%"></n-skeleton>
        <n-skeleton text width="40%"></n-skeleton>
        <n-skeleton text width="60%"></n-skeleton>
        <n-skeleton text width="66%"></n-skeleton>
    </n-space>
    <n-space vertical v-else>
        <!-- 发布时间，分享，收藏，更多操作 -->
        <n-card :bordered="false" size="small">
            <n-space justify="space-between" align="center">
                <!-- 发布时间 -->
                <n-space align="center" :wrap-item="false">
                    <n-icon :component="FiberManualRecordRound" color="#18a058"></n-icon>
                    <n-text depth="3">保存并发布于:{{ note.updateTime }}</n-text>
                </n-space>
                <!-- 功能按钮，分享收藏更多等 -->
                <n-space align="center" :wrap-item="false" :size="8">
                    <n-button round dashed> 分享</n-button>
                    <!-- 收藏按钮提示语句 -->
                    <n-popover>
                        <template #trigger>
                            <!-- 收藏按钮 -->
                            <n-button circle quaternary>
                                <n-icon size="20" :component="StarBorderRound"></n-icon>
                            </n-button>
                        </template>
                        收藏
                    </n-popover>

                    <n-button circle quaternary>
                        <n-icon size="20" :component="MoreHorizRound"></n-icon>
                    </n-button>
                </n-space>
            </n-space>
        </n-card>
        <!-- 富文本编辑器 -->
        <n-card :bordered="false" size="small">
            <ckeditor5 :editor="editorType" @ready="readyEditor" v-model="note.content" :config="getEditorConfigs()" />
        </n-card>
    </n-space>
</template>

<script setup>
import { noteBaseRequest } from "@/request/note_request"
import { getUserToken, loginInvalid } from "@/utils/UserLoginUtil"
import { useMessage, useLoadingBar } from 'naive-ui'
import { watch, ref, onMounted } from 'vue'
import { FiberManualRecordRound, StarBorderRound, MoreHorizRound } from "@vicons/material"
import CKEditor from '@ckeditor/ckeditor5-vue';
import { editorType, getEditorConfigs } from "@/editor"
//消息对象
const message = useMessage()
//加载条对象
const loadingBar = useLoadingBar()

const propsData = defineProps({
    id: { type: Number, required: true }
})
//需要监听如果笔记编号发生了改变，需要重新获取编辑笔记的信息
watch(
    () => propsData.id,
    () => {
        getEditNote()
    }
)

//注册ckeditor5组件
const ckeditor5 = CKEditor.component

const loading = ref(true)//是否处于加载中
/**
 * 获取编辑笔记的信息
 */
const getEditNote = async () => {
    loading.value = true;//处于加载中
    //判断用户的登录状态
    const userToken = await getUserToken()
    loadingBar.start()//加载条开始

    //发送获取编辑笔记的请求
    const { data: responseData } = await noteBaseRequest.get(
        "/note/edit",
        {
            params: { noteId: propsData.id },
            headers: { userToken }
        }
    ).catch(() => {
        loadingBar.error()//加载条异常结束
        //发送请求失败（404，500,400...）
        throw message.error("获取编辑笔记请求失败")//显示发送请求失败的通知
    })
    //得到服务器返回的数据，进行处理
    console.log(responseData)

    if (responseData.success) {
        loadingBar.finish()//加载条结束
        note.value = responseData.data
        loading.value = false//加载结束
    }
    else {
        loadingBar.error()//加载条异常结束
        message.error(responseData.message)//显示发送请求失败的通知
        if (responseData.code === "L_008") {
            loginInvalid(true)//登录失效
        }
    }
}
//获取编辑的笔记信息
getEditNote()

//编辑的笔记信息
const note = ref({})

//编辑器对象
let editor = null
/**
 * 富文本编辑器初始化完毕
 * @param {*} editorObj 编辑器的对象
 */
const readyEditor = (editorObj) => {
    editor = editorObj
    // 在编辑器区域插入工具栏
    editor.ui.getEditableElement().parentElement.insertBefore(
        editor.ui.view.toolbar.element,
        editor.ui.getEditableElement()
    );
}
//onMounted表示当页面加载完成后
onMounted(() => {
    window.addEventListener('keydown', (e) => {
        //让keyCode的值为83，并且有按住ctrl键，ctrl+s
        if (e.keyCode === 83 && e.ctrlKey) {
            //因为按住ctrl+s会出现一个界面，所以要阻止他的默认行为
            e.preventDefault()
            e.returnValue === false;
            //保存笔记 
            saveNote()
        }
    })
})

const saveNote = async () => {
    const noteId = propsData.id//编号
    const title = editor.plugins.get('Title').getTitle();//文档标题
    const body = editor.plugins.get('Title').getBody()//文档内容
    const content = note.value.content//文档内容完整
    //判断用户的登录状态
    const userToken = await getUserToken()
    loadingBar.start()//加载条开始

    //发送保存笔记笔记的请求
    const { data: responseData } = await noteBaseRequest.post(
        "/note/save",
        { noteId, title, body, content },
        {
            headers: { userToken }
        }
    ).catch(() => {
        loadingBar.error()//加载条异常结束
        //发送请求失败（404，500,400...）
        throw message.error("获取保存笔记请求失败")//显示发送请求失败的通知
    })
    //得到服务器返回的数据，进行处理
    console.log(responseData)

    if (responseData.success) {
        loadingBar.finish()//加载条结束
        message.success(responseData.message)//显示请求成功的通知
        note.updateTime = responseData.data
        location.reload()
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
/* 编辑器右下角的logo需要隐藏 */
.ck.ck-balloon-panel.ck-balloon-panel_visible {
    display: none;
}

/* 去掉编辑器的边框 */
.ck.ck-editor__editable_inline {
    border: none;
}

/* 编辑器聚焦时，去掉边框和盒子的阴影 */
.ck.ck-editor__editable.ck-focused:not(.ck-editor__nested-editable) {
    border: none;
    box-shadow: none;
}
</style>