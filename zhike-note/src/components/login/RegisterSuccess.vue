<template>
    <n-card style="text-align: center;">
        <n-icon size="120" color="#0e7a0d" :component="CheckCircleRound"></n-icon>
        <h4 style="margin-top: 0; margin-bottom: 5px;">欢迎加入知科笔记</h4>
        <n-text depth="3">
            请前往注册的邮箱，查看账号信息，
            <n-button text type="info" @click="eimts('changeStep', 1)">可点击此处前往登录</n-button>
        </n-text>
    </n-card>
</template>


<script setup>

import { CheckCircleRound } from '@vicons/material';
//自定义事件
const eimts = defineEmits(['changeStep'])
</script>