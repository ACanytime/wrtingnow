<template>
    <n-thing id="note-card">
        <template #header>
            <n-ellipsis line-clamp="1">
                {{ title }}
            </n-ellipsis>
        </template>
        <template #description>
            <n-ellipsis :tooltip="false" line-clamp="2">
                <n-text depth="3">
                    {{ htmlToText(desc) }}
                </n-text>
            </n-ellipsis>
        </template>
        <template #default>
            <n-space align="center" :size="8">
                <n-tag v-if="top" type="primary" size="small" :bordered="false">置顶</n-tag>
                <n-divider v-if="top" vertical></n-divider>
                <n-text depth="3">
                    {{ time }}
                </n-text>
            </n-space>
        </template>
    </n-thing>
</template>

<script setup>
//安装htmlToText包，可以删除文本中的html元素
import { htmlToText } from 'html-to-text'
//向父组件传值
defineProps({
    id: { type: Number, require: true },//编号
    title: { type: String, require: true },//标题
    desc: { type: String, default: () => "暂无内容" },//简介
    top: { type: Boolean, default: false },//是否置顶
    time: { type: String, require: true },//最后一次操作时间
});
</script>
<style>
.n-thing.note-card {
    word-break: break-all;
}
</style>