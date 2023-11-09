<template>
    <n-modal :show="show" preset="dialog" title="删除提醒" type="warning" :closable="false" :transform-origin="center">
        <template #icon>
            <n-icon :component="ReportGmailerrorredRound" style="position: relative; top: -2px;"></n-icon>
        </template>
        <template #default>{{ describe }}</template>
        <template #action>
            <!-- 彻底删除按钮 -->
            <n-button v-if="completeDeleteBtn" size="small" tertiary type="error"
                @click="emits('delete', true)">彻底删除</n-button>
            <!-- 删除按钮 -->
            <n-button v-if="deleteBtn" size="small" secondary type="error" @click="emits('delete', false)">删除</n-button>
            <!-- 取消按钮 -->
            <n-button size="small" tertiary @click="emits('cancel')">取消</n-button>
        </template>
    </n-modal>
</template>


<script setup>
import { ReportGmailerrorredRound } from '@vicons/material';
import { computed } from 'vue'
//自定义属性 父类向子类传递的参数
const propsData = defineProps({
    show: { type: Boolean, default: false },//是否显示删除提醒框
    title: { type: String }, //删除单个文件的标题
    size: { type: Number, default: 1 },//删除文件的个数
    deleteBtn: { type: Boolean, default: true },//删除按钮是否显示
    completeDeleteBtn: { type: Boolean, default: true },//彻底删除按钮是否显示
})
//自定义事件，点击的是哪个按钮,彻底删除事件，删除事件，取消事件
const emits = defineEmits(['delete', 'cancel'])

const describe = computed(() => {
    if (propsData.size === 1) {
        //删除单个文件
        if (propsData.deleteBtn && propsData.completeDeleteBtn) {
            //删除按钮和彻底删除按钮同时存在
            return '删除《' + propsData.title + '》，可在回收站中恢复，彻底删除则无法恢复！'
        }
        if (!propsData.deleteBtn && propsData.completeDeleteBtn) {
            //没有删除按钮但是有彻底删除按钮(回收站)
            return '彻底删除《' + propsData.title + '》无法恢复！'
        }
        return '删除提醒'
    } else {
        //删除多个文件
        return '删除多个文件提醒'
    }
})
</script>

<style scoped></style>