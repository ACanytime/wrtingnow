<template>
    <n-card>
        <!-- 前往注册 -->
        <n-space justify="space-between" align="center">
            <h2>登录</h2>
            <n-text depth="3">暂无账号？
                <n-button text type="info" @click="eimts('changeStep', 2)">前往注册</n-button>
            </n-text>
        </n-space>
        <!-- 登录表单 -->
        <n-form :model="loginFormValue" :rules="loginFormRules" ref="loginFormRef">
            <n-form-item label="邮箱号码" path="email" first>
                <n-input placeholder="请输入邮箱号码" v-model:value="loginFormValue.email">
                    <template #prefix>
                        <n-icon :component="EmailOutlined"></n-icon>
                    </template>
                </n-input>
            </n-form-item>
            <n-form-item label="密码" path="password">
                <n-input type="password" placeholder="请输入密码" v-model:value="loginFormValue.password">
                    <template #prefix>
                        <n-icon :component="LockOpenOutlined"></n-icon>
                    </template>
                </n-input>
            </n-form-item>
            <n-form-item :show-label="false" path="trim">
                <n-checkbox v-model:checked="loginFormValue.trim">同意本公司的</n-checkbox>
                <n-button text type="info">《条款与协议》</n-button>
            </n-form-item>
            <n-form-item :show-label="false">
                <n-button block type="success" :disabled="loginBtnDisabled" @click="toLogin">登录</n-button>
            </n-form-item>
        </n-form>
        <!-- 忘记密码 -->
        <n-space justify="center" style="cursor: pointer ;">
            <n-text depth="3">忘记密码</n-text>
        </n-space>
    </n-card>
</template>
<script setup>
import { EmailOutlined, LockOpenOutlined } from "@vicons/material"
import { ref } from 'vue'
import { noteBaseRequest } from "@/request/note_request"
import { useMessage, useLoadingBar } from 'naive-ui'
import { useLoginModalStore } from "../../stores/LoginModalStore"
import { useUserStore } from "../../stores/userStore"
//消息对象
const message = useMessage()
//加载条对象
const loadingBar = useLoadingBar()
//自定义事件
const eimts = defineEmits(['changeStep'])
//登录模态框共享资源对象
const loginModalStore = useLoginModalStore()
//改变登录模态框显示的状态（函数）
const { changeLoginModalShowStatus } = loginModalStore
//用户共享的资源对象
const userStore = useUserStore()
//改变用户信息的函数
const { setUserInfo } = userStore
//登录表单值
const loginFormValue = ref({
    email: '',
    password: '',
    trim: false
})
//登录表单规则
const loginFormRules = {
    email: [{
        required: true,
        message: "请输入邮箱号码",
        trigger: ["input", "blur"]
    },
    {
        message: "请输入正确的邮箱格式",
        trigger: ["input", "blur"],
        validator: (rule, value) => {
            return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(value)
        }
    }],
    password: {
        required: true,
        message: "请输入密码",
        trigger: ["input", "blur"]
    },
    trim: {

        message: "请认真阅读本公司的条款与协议",
        trigger: "change",
        validator: (rule, value) => {
            return value
        }
    }

}
//禁用登录按钮
const loginBtnDisabled = ref(false)
//去登录
const loginFormRef = ref(null)
const toLogin = (e) => {
    e.preventDefault();
    loginFormRef.value?.validate(async (errors) => {
        if (!errors) {
            loadingBar.start()//加载条开始
            loginBtnDisabled.value = true //禁用登录按钮
            //发送登录请求
            const { data: responseData } = await noteBaseRequest.post(
                "/user/login/email/password",
                {
                    "email": loginFormValue.value.email,
                    "password": loginFormValue.value.password
                }
            ).catch(() => {
                loadingBar.error()//加载条异常结束
                //发送请求失败（404，500,400...）
                message.error("发送请求失败")

                setTimeout(() => {
                    loginBtnDisabled.value = false
                }, 1500)//解除禁用登录按钮
                throw "发送登录请求失败"
            })
            //得到服务器返回的数据，进行处理

            if (responseData.success) {
                loadingBar.finish()//加载条结束
                message.success(responseData.message)
                changeLoginModalShowStatus(false)//关闭登录模态框


                const user = responseData.data.user//登录的用户信息

                setUserInfo(
                    responseData.data.userTokenKey,
                    user.id,
                    user.email,
                    user.nickname,
                    user.headPic,
                    user.level,
                    user.time
                )
            }
            else {
                loadingBar.error()//加载条异常结束
                message.error(responseData.message)
            }
            setTimeout(() => {
                loginBtnDisabled.value = false
            }, 1500)//解除禁用登录按钮
        }
    })
}
</script>