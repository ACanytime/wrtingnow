<template>
    <n-modal v-model:show="show" :auto-focus="false" :on-after-leave="resetEditThing" transform-origin="center"
        :close-on-esc="false" :mask-closable="false">
        <div>
            <!-- 骨架屏卡片 -->
            <n-card v-show="loading" size="small" :bordered="false" style="width: 460px;">

                <template #default>
                    <!-- 小记标题骨架屏 -->
                    <div style="padding: 0 14px;">
                        <n-skeleton :height="40" :sharp="false" />
                    </div>

                    <div style="padding:10px 14px 0">
                        <!-- 置顶、标签容器 -->
                        <n-space align="center">
                            <!-- 置顶文本骨架屏 -->
                            <n-skeleton :height="14" :width="42" />
                            <!-- 置顶开关骨架屏 -->
                            <n-skeleton :height="22" :width="40" :sharp="false" />

                            <!-- 标签容器 -->
                            <n-space align="center">
                                <!-- 标签文本骨架屏 -->
                                <n-skeleton :height="14" :width="42" />
                                <!-- 便签创建骨架屏 -->
                                <n-skeleton :height="28" :width="38" :sharp="false" />
                            </n-space>
                        </n-space>
                        <!-- 分割线 -->
                        <n-divider style="margin-top:14px"></n-divider>
                        <!-- 待办事项 照抄照搬 -->
                        <n-skeleton :height="34" :sharp="false" />
                    </div>
                </template>
                <template #action>
                    <n-grid cols="2" :x-gap="12">
                        <n-gi>
                            <n-skeleton :height="34" :sharp="false" />
                        </n-gi>
                        <n-gi>
                            <n-skeleton :height="34" :sharp="false" />
                        </n-gi>
                    </n-grid>
                </template>
            </n-card>
            <!-- 编辑小记卡片 -->
            <n-card v-show="!loading" size="small" :bordered="false" style="width: 460px;"
                :class="{ 'thing-card-finished': formValue.finished }">

                <template #default>
                    <n-form ref="formRef" :model="formValue" :rules="formRules">
                        <n-form-item :show-label="false" :show-feedback="false" path="title">
                            <!-- 小记标题 -->
                            <n-input size="large" placeholder="请输入小记标题" v-model:value="formValue.title"
                                style="--n-border:none;background-color: transparent;"></n-input>
                        </n-form-item>

                        <div style="padding:10px 14px 0">
                            <!-- 置顶、标签容器 -->
                            <n-space align="center">
                                <n-text depth="3">置顶：</n-text>
                                <n-switch :round="false" v-model:value="formValue.top"></n-switch>
                                <!-- 标签容器 -->
                                <n-space align="center">
                                    <n-form-item :show-label="false" :show-feedback="false" path="tags">
                                        <n-text>标签：</n-text>
                                        <n-dynamic-tags :max="5" v-model:value="formValue.tags"
                                            :color="{ borderColor: 'rgba(0,0,0,0)' }"></n-dynamic-tags>
                                    </n-form-item>

                                </n-space>
                            </n-space>
                            <!-- 分割线 -->
                            <n-divider style="margin-top:14px"></n-divider>
                            <!-- 待办事项 照抄照搬 -->
                            <n-form-item :show-label="false" :show-feedback="false" path="content">
                                <n-dynamic-input :on-create="onCreateTuDoThing" v-model:value="formValue.content">
                                    <template #create-button-default>
                                        添加一个待办事项
                                    </template>
                                    <template #default="{ value }">
                                        <div style="display: flex; align-items: center; width: 100%">
                                            <!-- 复选框（是否完成这个待办事项） -->
                                            <n-checkbox v-model:checked="value.checked" />
                                            <!-- 待办事项文本输入框 -->
                                            <n-input placeholder="请输入......" v-model:value="value.thing"
                                                style="margin-left: 12px;--n-border:none" />
                                        </div>
                                    </template>
                                    <template #action="{ index, create, remove }">
                                        <div style="display: flex; align-items: center; margin-left: 12px;">
                                            <!-- 添加代办事项按钮 -->
                                            <n-button circle tertiary type="tertiary" @click="() => create(index)"
                                                style="margin-right: 6px;">
                                                <n-icon :component="AddBoxRound"></n-icon>
                                            </n-button>
                                            <!-- 删除待办事项按钮 -->
                                            <n-button circle tertiary type="tertiary" @click="() => remove(index)">
                                                <n-icon :component="DeleteForeverFilled"></n-icon>
                                            </n-button>
                                        </div>
                                    </template>
                                </n-dynamic-input>
                            </n-form-item>

                        </div>
                    </n-form>



                </template>
                <template #action>
                    <n-grid cols="2" :x-gap="12">
                        <n-gi>
                            <n-button block tertiary @click="show = false">取消</n-button>
                        </n-gi>
                        <n-gi>
                            <n-button :disabled="saveBtnDisabled" block ghost type="primary"
                                @click="saveEditThing">保存</n-button>
                        </n-gi>
                    </n-grid>
                </template>
            </n-card>
        </div>

    </n-modal>
</template>
<script setup>
import { ref, computed, h, onBeforeMount } from 'vue'
import { AddBoxRound, DeleteForeverFilled } from '@vicons/material'
import { useNotification, NText, NSpace } from 'naive-ui'
import { getUserToken, loginInvalid } from "@/utils/UserLoginUtil"
import { noteBaseRequest } from "@/request/note_request"
import { useMessage, useLoadingBar } from 'naive-ui'
import bus from 'vue3-eventbus'
import { disabledBtn } from "@/utils/disabledBtn"

//消息对象
const message = useMessage()
//加载条对象
const loadingBar = useLoadingBar()

//利用vue3中的组件eventbus监听是否触发了新增小记函数
bus.on('newCreateThing', () => {
    showEditModal(null)
})
//组件卸载之前
onBeforeMount(() => {
    bus.on('newCreateThing')//停止监听新建小记事件
})
//通知对象
const notification = useNotification()
//创建一个待办事项
const onCreateTuDoThing = () => ({
    checked: false,//是否已完成（选中）
    thing: ''//待办事项
})
//该小记用户的编号
const userId = ref(null)
//小记的编号
const thingId = computed(() => formValue.value.id)
//编辑小记表单值
const formValue = ref({
    id: null,//小记编号（新增小记的时候为null）
    title: '',//标题
    top: false,//是否置顶
    tags: [],//标签
    content: [],//待办事项
    finished: computed(() => {
        const content = formValue.value.content//待办事项
        if (content.length === 0) return false //如果没有待办事项，则为未完成
        return content.every(item => item.checked)//如果有待办事项，判断是否都被选择，如果都被选中，则为已完成
    })
})
//编辑小记的表单引用
const formRef = ref(null)

//编辑小记表单的验证规则
const formRules = {
    title: {
        required: true,
        message: '请设置编辑小记的标题'
    },
    tags: {
        required: true,
        message: '请设置编辑小记的标签'
    },
    content: {
        required: true,
        message: '请设置编辑小记的待办事项'
    }
}

//保存编辑的小记
const saveEditThing = () => {
    formRef.value?.validate(errors => {
        if (!errors) {
            if (formValue.value.id === null) {
                //新增小记的保存
                newCreateSave()
            }
            else {
                //修改小记的保存
                updateSave()
            }

        }
        else {
            //表单中所有验证错的对象 
            const errorsMessage = [].concat(...errors)
            console.log(errorsMessage)
            //显示错误的通知
            notification.error({
                title: '编辑小记保存提醒',
                content: () => h(
                    NSpace,
                    { vertical: true },
                    {
                        default: () => errorsMessage.map(
                            (item, index) => h(
                                NText,
                                { type: 'error' },
                                {
                                    default: () => (index + 1) + ":" + item.message
                                }
                            )
                        )
                    }
                )
            })
        }
    })
}
//是否处于加载状态中
const loading = ref(true)

//是否显示编辑小记模态框
const show = ref(false)
/**
 * 显示编辑小记的模态框
 * @param id 小记编号（无值，则是点击新增小记，有值，则为修改小记）
 */
const showEditModal = id => {
    show.value = true;//显示编辑小记的模态框
    loading.value = true;//加载
    //判断是修改小记还是新增小记
    if (id == null) {
        console.log('新增小记')
        loading.value = false //加载已结束
    }
    else {
        formValue.value.id = id //需要修改小记的编号
        console.log('修改小记' + id)
        //发送请求（根据小记编号获取最新的小记信息）
        getEditThing(id)

    }
}
//因为是组合式，所有数据源都是私有的，父类调用子类需要导出
defineExpose({ showEditModal, show, userId, thingId })

//重置编辑的小记
const resetEditThing = () => {
    formValue.value.id = null//小记编号
    formValue.value.title = ''//标题
    formValue.value.top = false//是否置顶
    formValue.value.tags = []//标签
    formValue.value.content = []//待办事项
}
//自定义事件
const emits = defineEmits(['save'])
//新增小记的保存
const newCreateSave = async () => {
    //判断用户的登录状态
    const userToken = await getUserToken()

    loadingBar.start()//加载条开始
    //发送创建小记请求
    const title = formValue.value.title;
    const top = formValue.value.top;
    const tags = formValue.value.tags.join();//['IT','计算机','科学']=>'IT,计算机,科学'
    const content = JSON.stringify(formValue.value.content)//[{...},{...}]=>'[{...},{...}]'
    const finished = formValue.value.finished;
    const { data: responseData } = await noteBaseRequest.post(
        "/thing/create",
        { title, top, tags, content, finished },
        {
            headers: { userToken }
        }
    ).catch(() => {
        loadingBar.error()//加载条异常结束
        //发送请求失败（404，500,400...）
        throw message.error("新增小记请求失败")//显示发送请求失败的通知
    })
    //得到服务器返回的数据，进行处理
    console.log(responseData)

    if (responseData.success) {
        loadingBar.finish()//加载条结束
        message.success(responseData.message)//显示发送请求成功的通知
        show.value = false//关闭编辑小记窗口
        //触发保存事件  重新获取小记列表
        emits('save', true)
    }
    else {
        loadingBar.error()//加载条异常结束
        message.error(responseData.message)//显示发送请求失败的通知
        if (responseData.code === "L_008") {
            loginInvalid(true)//登录失效
        }
    }
}
//获取编辑小记的信息（修改小记的最新信息）
//thingId 为小记编号
const getEditThing = async (thingId) => {
    //判断用户的登录状态
    const userToken = await getUserToken()

    loadingBar.start()//加载条开始
    //发送获取编辑小记请求

    const { data: responseData } = await noteBaseRequest.get(
        "/thing/edit",
        {
            params: { thingId },
            headers: { userToken }
        }
    ).catch(() => {
        loadingBar.error()//加载条异常结束
        show.value = false//关闭编辑小记的窗口
        //发送请求失败（404，500,400...）
        throw message.error("获取编辑小记请求失败")//显示发送请求失败的通知
    })
    //得到服务器返回的数据，进行处理
    console.log(responseData)

    if (responseData.success) {
        loadingBar.finish()//加载条结束
        const editThing = responseData.data
        userId.value = editThing.userId//当前小记的用户编号
        formValue.value.title = editThing.title//标题
        formValue.value.top = !!editThing.top//置顶
        formValue.value.tags = editThing.tags.split(',')//标签 'IT,计算机，科学'
        formValue.value.content = JSON.parse(editThing.content)//内容
        loading.value = false //加载已结束
    }
    else {
        loadingBar.error()//加载条异常结束
        show.value = false//关闭编辑小记的窗口
        message.error(responseData.message)//显示发送请求失败的通知
        if (responseData.code === "L_008") {
            loginInvalid(true)//登录失效
        }
    }
}
//保存按钮是否需要禁用
const saveBtnDisabled = ref(false)
//修改小记的保存
const updateSave = async () => {
    //判断用户的登录状态
    const userToken = await getUserToken()

    loadingBar.start()//加载条开始
    disabledBtn(saveBtnDisabled, true)//禁用保存按钮
    //发送创建小记请求
    const thingId = formValue.value.id
    const title = formValue.value.title;
    const top = formValue.value.top;
    const tags = formValue.value.tags.join();//['IT','计算机','科学']=>'IT,计算机,科学'
    const content = JSON.stringify(formValue.value.content)//[{...},{...}]=>'[{...},{...}]'
    const finished = formValue.value.finished;
    const { data: responseData } = await noteBaseRequest.post(
        "/thing/update",
        { thingId, title, top, tags, content, finished },
        {
            headers: { userToken }
        }
    ).catch(() => {
        loadingBar.error()//加载条异常结束
        disabledBtn(saveBtnDisabled, false, true, 2)//解除禁用保存按钮
        //发送请求失败（404，500,400...）
        throw message.error("修改小记请求失败")//显示发送请求失败的通知
    })
    //得到服务器返回的数据，进行处理
    console.log(responseData)
    disabledBtn(saveBtnDisabled, false, true, 2)//解除禁用保存按钮
    if (responseData.success) {
        loadingBar.finish()//加载条结束
        message.success(responseData.message)//显示发送请求成功的通知
        show.value = false//关闭编辑小记窗口
        //触发修改事件 不是保存  不用重新获取小记列表
        emits('save', false)
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

<style scoped>
.thing-card-finished {
    background-image: url("@/assets/finish.png");
    background-position: 360px 0;
    background-repeat: no-repeat;
    animation: finished 0.25s linear forwards;
}

@keyframes finished {
    from {
        background-size: 130px 130px;
    }

    to {
        background-size: 100px 100px;
    }
}
</style>