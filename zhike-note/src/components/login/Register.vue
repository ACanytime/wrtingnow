<template>
    <n-card>
        <!-- 前往登录 -->
        <n-space justify="space-between" align="center">
            <h2>注册</h2>
            <n-text depth="3">已有账号？
                <n-button text type="info" @click="eimts('changeStep', 1)">前往登录</n-button>
            </n-text>
        </n-space>
        <!-- 注册表单 -->
        <n-form :model="registerFormValue" :rules="registerFormRules" ref="registerFormRef">
            <n-form-item label="邮箱号码" path="email" first>
                <n-input placeholder="请输入邮箱号码" v-model:value="registerFormValue.email">
                    <template #prefix>
                        <n-icon :component="EmailOutlined"></n-icon>
                    </template>
                </n-input>
            </n-form-item>
            <n-grid :cols="2" :x-gap="24">
                <n-form-item-gi label="验证码" path="vc">
                    <n-input placeholder="请输入验证码" v-model:value="registerFormValue.vc"></n-input>
                </n-form-item-gi>
                <n-form-item-gi>
                    <n-button block secondary type="success" :disabled="btnCountDown.disabled" @click="getEmailVC">{{
                        btnCountDown.text
                    }}</n-button>
                </n-form-item-gi>
            </n-grid>

            <n-form-item :show-label="false" path="trim">
                <n-checkbox v-model:checked="registerFormValue.trim">同意本公司的</n-checkbox>
                <n-button text type="info">《条款与协议》</n-button>
            </n-form-item>
            <n-form-item :show-label="false">
                <n-button block type="success" :disabled="registerBtnDisabled" @click="toRegister">注册</n-button>
            </n-form-item>
        </n-form>
        <!-- 忘记密码 -->
        <n-space justify="center" style="cursor: pointer ;">
            <n-text depth="3">忘记密码</n-text>
        </n-space>
    </n-card>
</template>
<script setup>
import { EmailOutlined } from "@vicons/material"
import { ref } from 'vue'
import { useMessage, useLoadingBar } from 'naive-ui'
import { noteBaseRequest } from "@/request/note_request"
//消息对象
const message = useMessage()
//加载条对象
const loadingBar = useLoadingBar()
//自定义事件
const eimts = defineEmits(['changeStep'])
//验证码查询的关键词
const emailVcKey = ref('')
//注册表单值
const registerFormValue = ref({
    email: '',
    vc: '',
    trim: false
})
//注册表单规则
const registerFormRules = {
    email: [{
        required: true,
        message: "请输入邮箱号码",
        trigger: ["input", "blur"]
    },
    {
        key: 'mail',
        message: "请输入正确的邮箱格式",
        trigger: ["input", "blur"],
        validator: (rule, value) => {
            return /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(value)
        }
    }],
    vc: {
        required: true,
        message: "请输入验证码",
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
//注册按钮是否禁用
const registerBtnDisabled = ref(false)
//注册表单引用
const registerFormRef = ref(null)
//去注册
const toRegister = (e) => {
    e.preventDefault();
    registerFormRef.value?.validate(async (errors) => {
        if (!errors) {
            //是否获取验证码
            if (emailVcKey.value === '' || emailVcKey === null) {
                throw message.error("请先获取验证码")
            }
            //判断接受验证码的邮箱是否和注册邮箱一致
            const vc_email = emailVcKey.value.split(":")[1];//接受验证码的邮箱
            const email = registerFormValue.value.email;//注册账号的邮箱
            if (email !== vc_email) {
                throw message.error("注册邮箱号发生改变，请重新获取");
            }
            loadingBar.start()//加载条开始
            registerBtnDisabled.value = true //禁用注册按钮
            //发送注册请求
            const { data: responseData } = await noteBaseRequest.post(
                "/user/register/email",
                {
                    email,
                    vc: registerFormValue.value.vc,
                    vcKey: emailVcKey.value
                }
            ).catch(() => {
                loadingBar.error()//加载条异常结束
                //发送请求失败（404，500,400...）
                message.error("发送注册请求失败")

                setTimeout(() => {
                    registerBtnDisabled.value = false
                }, 1500)//解除禁用注册按钮
                throw "发送注册请求失败"
            })
            //得到服务器返回的数据，进行处理
            console.log(responseData)
            if (responseData.success) {
                loadingBar.finish()//加载条结束
                eimts('changeStep', 3)//跳转到注册成功的界面
            }
            else {
                loadingBar.error()//加载条异常结束
                message.error(responseData.message)
                setTimeout(() => {
                    registerBtnDisabled.value = false
                }, 1500)//解除禁用注册按钮
            }

        }
    })

}
//--------获取验证码--------
//按钮状态
const btnCountDown = ref({
    text: '获取验证码',//按钮显示的文本
    time: 60,//还有多少秒结束
    disabled: false,//是否禁用按钮,true为禁用
    clock: null
})
//按钮倒计时
const buttonCountDown = () => {
    btnCountDown.value.clock = setInterval(() => {
        if (btnCountDown.value.time === 1) {

            //不需要倒计时了--重置获取验证码的状态
            resetButtonCountDownStatus()
        }
        else {
            //需要倒计时
            btnCountDown.value.disabled = true //禁用按钮
            btnCountDown.value.time--//时间递减 
            btnCountDown.value.text = btnCountDown.value.time + '秒重新获取'//按钮显示文本
        }
    }, 1000)
}
//重置验证码的状态
const resetButtonCountDownStatus = () => {
    //清除任务
    clearInterval(btnCountDown.value.clock)
    btnCountDown.value.text = "获取验证码"
    btnCountDown.value.time = 60
    btnCountDown.value.disabled = false
}
//获取验证码规则
const getEmailVC = () => {
    registerFormRef.value?.validate(
        async (errors) => {
            if (!errors) {
                buttonCountDown()//按钮倒计时

                loadingBar.start()//加载条开始

                //发送获取邮箱验证码的请求
                const { data: responseData } = await noteBaseRequest.get(
                    "/mail/register/vc",
                    {
                        params: {
                            email: registerFormValue.value.email
                        }
                    }
                ).catch(() => {
                    loadingBar.error()//加载条异常结束
                    //发送请求失败（404，500,400...）
                    message.error("发送验证码失败")


                })
                //得到服务器返回的数据，进行处理
                console.log(responseData)
                if (responseData.success) {
                    loadingBar.finish()//加载条结束
                    message.success(responseData.message)//显示发送成功的通知
                    emailVcKey.value = responseData.data


                }
                else {
                    loadingBar.error()//加载条异常结束
                    message.error(responseData.message)//显示发送失败的通知
                }

            }
        },
        (rule) => {
            return rule?.key === 'mail';
        }
    )
}
</script>