<template>
    <n-modal v-model:show="showLoginModal" :mask-closable="false" transform-origin="center" :close-on-esc="false">
        <div style="width:400px">
            <Transition name="bounce" mode="out-in">
                <component :is="showLoginModalCard" @changeStep="changeLoginModalStep"></component>
            </Transition>

        </div>
    </n-modal>
</template>

<script setup>
import { ref, computed } from "vue";
import Login from "@/components/login/Login.vue"
import Register from "@/components/login/Register.vue"
import RegisterSuccess from '@/components/login/RegisterSuccess.vue';
import { useLoginModalStore } from "../../stores/LoginModalStore";
import { storeToRefs } from 'pinia';

//登录模态框共享资源的对象
const loginModalStore = useLoginModalStore()

//是否显示登录模态框
const { showLoginModal } = storeToRefs(loginModalStore)
//登录模态框显示的内容（1：代表登录，2：代表注册，3：注册成功）
const loginModalStep = ref(1)
//登录模态框显示的卡片
const showLoginModalCard = computed(() => {
    switch (loginModalStep.value) {
        case 1:
            return Login;
        case 2:
            return Register;

        default:
            return RegisterSuccess;
    }
})
//改变登录模态框显示的卡片
const changeLoginModalStep = step => {
    loginModalStep.value = step
}
</script>
<style>
.bounce-enter-active {
    animation: bounce-in 0.5s;
}

.bounce-leave-active {
    animation: bounce-in 0.5s reverse;
}

@keyframes bounce-in {
    0% {
        transform: scale(0);
    }

    50% {
        transform: scale(1.25);
    }

    100% {
        transform: scale(1);
    }
}
</style>