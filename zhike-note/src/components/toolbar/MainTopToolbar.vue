<template>
    <n-space align="center" justify="space-between" style="height: 100%;">
        <n-text>cloudy云笔记</n-text>
        <n-space align="center" :wrap-item="false">
            <!-- 头像 -->
            <!-- 点击头像弹出信息 -->
            <n-popover v-model:show="userMenuShow" trigger="click" width="260" content-style="padding: 10px">
                <template #trigger>
                    <n-button circle :bordered="false">
                        <n-avatar v-if="user_id !== null" round :src="head_image" />
                    </n-button>


                </template>
                <!-- 具体信息 -->
                <!-- thing东西布局 -->
                <n-thing :title="nickName">
                    <!-- 头像 -->
                    <template #avatar>
                        <n-avatar size="large" round :src="head_image" style="position: relative; top: 3px;" />
                    </template>
                    <!-- 简介 -->
                    <template #description>
                        <n-space align="center">
                            <n-tag size="small" :bordered="false" type="success">{{ levelInfo }}</n-tag>
                            <n-text depth="3">2099-12-31 到期</n-text>
                        </n-space>
                    </template>
                    <template #default>
                        <!-- --分割线-- -->
                        <n-divider style="margin: 5px auto;"></n-divider>
                        <!-- 菜单选项 -->
                        <n-menu id="user-header-menu" :options="userMenu" :on-update:value="clickUserMenu"></n-menu>
                    </template>

                </n-thing>
            </n-popover>

            <!--分割线-->
            <n-divider v-if="user_id !== null" vertical></n-divider>
            <!-- 消息 -->
            <n-badge dot processing type="success" :offest="[-8, 4]">
                <n-button circle type="tertiary">
                    <n-icon size="18" :component="NotificationsNoneOutlined"></n-icon>
                </n-button>
            </n-badge>
            <!-- 主题按钮 -->
            <n-button circle type="tertiary" @click="changeTheme(!isDarkTheme)">
                <n-icon size="18" :component="theme.icon"></n-icon>
            </n-button>
            <!-- 登录按钮 -->
            <n-button v-if="user_id === null" type="primary" @click="changeLoginModalShowStatus(true)">登录</n-button>
        </n-space>

    </n-space>
</template>
<style scoped>
.n-menu#user-header-menu:deep(.n-menu-item-content) {
    padding-left: 18 !important;

}
</style>

<script setup>
import { NotificationsNoneOutlined, AccountBoxFilled, ManageAccountsFilled, LogOutRound } from '@vicons/material'
import { useThemeStore } from "@/stores/themeStore";
import { storeToRefs } from "pinia";
import { useLoginModalStore } from '../../stores/LoginModalStore';
import { useUserStore } from "../../stores/userStore"
import { NIcon } from 'naive-ui';
import { noteBaseRequest } from "@/request/note_request";
import { useMessage, useLoadingBar } from 'naive-ui'
import { ref } from 'vue'
import { loginInvalid, getUserToken } from "@/utils/userLoginUtil"
import { useRouter } from 'vue-router';
const router = useRouter()//路由对象

//消息对象
const message = useMessage()
//加载条对象
const loadingBar = useLoadingBar()
//登录模态框共享资源的对象
const loginModalStore = useLoginModalStore()

//用户的共享数据对象
const userStore = useUserStore()
const { id: user_id, head_image, nickName, levelInfo } = storeToRefs(userStore)
//取出重置用户信息的函数
const { resetUserInfo } = userStore
//改变登录模态框的登录状态
const { changeLoginModalShowStatus } = loginModalStore
const themeStore = useThemeStore()
const { theme, isDarkTheme } = storeToRefs(themeStore)
const { changeTheme } = themeStore

//读图标，在菜单里面要以函数的形式来读取图标
const renderIcon = icon => {
    return () => h(NIcon, null, { default: () => h(icon) })
}
//是否显示用户菜单弹出信息
const userMenuShow = ref(false)
//用户菜单选项
const userMenu = [
    {
        key: 'user-center',
        label: '个人中心',
        icon: renderIcon(AccountBoxFilled)
    },
    {
        key: 'account-setting',
        label: '账户设置',
        icon: renderIcon(ManageAccountsFilled)
    },
    {
        key: 'sign-out',
        label: '退出登录',
        icon: renderIcon(LogOutRound)
    },
]
//用户菜单选项选中的回调
const clickUserMenu = (key) => {
    if (key === 'user-center') {
        router.push("/"); // 将路由导航到首页页面
    }

    if (key === 'sign-out') {
        userMenuShow.value = false
        //退出登录
        signOutLogin()
    }
}

//退出登录
const signOutLogin = async () => {
    //判断用户是否已登录(客户端检查本地存储userToken的值)
    const userToken = await getUserToken()
    loadingBar.start()//加载条开始
    //删除redis中对应的key（发送退出登录请求）
    const { data: responseData } = await noteBaseRequest.get(
        "/user/login/out",
        {
            headers: { userToken }
        }
    ).catch(() => {
        loadingBar.error()//加载条异常结束
        throw message.error("退出登录错误")
    })
    console.log(responseData)

    if (responseData.success) {
        loadingBar.finish()//加载条结束
        //用户共享的数据清空
        loginInvalid(true)//登录失效
        //userToken本地存储删除 
        localStorage.removeItem("userToken")
    }
    else {
        loadingBar.error()//加载条异常结束
        message.error(responseData.message)//显示退出登录失败的消息
    }

}


</script>
