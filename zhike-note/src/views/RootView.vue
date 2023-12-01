<template>
    <!-- 布局 -->
    <n-layout position="absolute">
        <!--应用头-->
        <n-layout-header bordered style="height: 64px; padding:0 20px">
            <MainTopToolbar></MainTopToolbar>

        </n-layout-header>
        <!-- 左侧应用栏 -->
        <n-layout position="absolute" style="top: 64px;" has-sider>
            <n-layout-sider bordered width="64px" text-align:center content-style="padding: 24px;">
                <MainLeftToolbar>

                </MainLeftToolbar>
            </n-layout-sider>

            <!-- 主页面 -->
            <router-view>
            </router-view>

        </n-layout>
    </n-layout>

    <!-- 登录模态框 -->
    <LoginModal></LoginModal>
</template>


<script setup>
import MainTopToolbar from '@/components/toolbar/MainTopToolbar.vue'
import LoginModal from '@/components/login/LoginModal.vue'
import MainLeftToolbar from '@/components/toolbar/MainLeftToolbar.vue'
import { useDialog } from 'naive-ui';
import { ref, inject, watch } from 'vue';

//对话框对象
const dialog = useDialog()
//接受祖先组件提供的数据：是否重新加载页面
const needReLoad = inject('needReLoad')

//如果needReLoad的值为true，则显示重新加载页面的对话框
watch(
    () => needReLoad.value,
    newData => {
        if (needReLoad.value === true) changeLoginStatusDialog()//弹出重新加载页面的对话框

    }
)
//登录状态发生改变的对话框
const changeLoginStatusDialog = () => {
    dialog.destroyAll()//销毁已出现的所有的对话框，避免多个对话框的出现
    dialog.create({
        showIcon: false,
        content: "登录状态已改变，需要重新加载页面!",
        positiveText: "重新加载页面",
        positiveButtonProps: {
            tertiary: true
        },
        closable: false,
        closeOnEsc: false,
        maskClosable: false,
        onPositiveClick: () => {
            //重新加载页面
            window.location.reload()

        }
    })
}

</script>