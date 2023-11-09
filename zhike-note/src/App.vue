<script setup>
import RootView from "@/views/RootView.vue"
import { useThemeStore } from '@/stores/themeStore'
import { useUserStore } from '@/stores/userStore'
import { storeToRefs } from "pinia"
import { onMounted, ref, provide, watch } from 'vue'
import { useRouter } from 'vue-router';

//主题的共享资源
const themeStore = useThemeStore()
//用户的共享资源
const userStore = useUserStore()
//主题
const { theme } = storeToRefs(themeStore)

const router = useRouter()//路由对象
const routerPath = ref(router.currentRoute.value.path)//路由地址 
watch(
  () => router.currentRoute.value,
  newData => {
    routerPath.value = newData.path
  }
)
//为后代提供路由地址的数据
provide('routerPath', routerPath)


//是否需要重新加载页面
const needReLoad = ref(false)
//为后代组件提供数据或者函数
provide('needReLoad', needReLoad)
//当一个页面的主题或者用户发生改变时，另外一个页面也要做出相对应的改变
onMounted(() => {
  window.addEventListener('storage', event => {
    //监听主题是否发生改变
    if (event.key === 'theme') {
      themeStore.$hydrate()//将本地存储的主题数据恢复到store中

      // const newTheme = JSON.parse(event.newValue)//新值
      // changeTheme(newTheme.isDarkTheme)//改变主题
    }
    else if (event.key === 'user') {
      //判断是否token值发生了变化
      const newToken = JSON.parse(event.newValue).token
      const oldToken = JSON.parse(event.oldValue).token
      if (newToken === oldToken) {
        //将本地存储的用户数据恢复到Store中
        userStore.$hydrate()
      } else {
        //需要重新加载页面
        needReLoad.value = true
        setTimeout(() => {
          needReLoad.value = false
        }, 1000)
      }
    }
  })
})

</script>

<template>
  <n-config-provider :theme="theme.name">
    <n-loading-bar-provider>
      <n-notification-provider>
        <n-dialog-provider>
          <n-message-provider>
            <RootView></RootView>
          </n-message-provider>

        </n-dialog-provider>

      </n-notification-provider>

    </n-loading-bar-provider>

  </n-config-provider>
</template>

