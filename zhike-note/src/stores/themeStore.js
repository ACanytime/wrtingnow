import { DarkModeRound, LightbulbOutlined } from '@vicons/material'
import { darkTheme } from 'naive-ui'
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
//关于主题的全局状态
export const useThemeStore = defineStore(
  "theme",
  () => {
    //是否是暗系主题

    const isDarkTheme = ref(false)
    //暗系主题和灯泡，亮系主题和月牙
    const theme = computed(() => {
      if (isDarkTheme.value) {
        //暗系主题
        return {
          name: darkTheme,
          icon: LightbulbOutlined
        }

      }
      else {
        //亮系主题
        return {
          name: null,
          icon: DarkModeRound
        }
      }
    })
    //更改主题
    const changeTheme = dark => {
      isDarkTheme.value = dark
    }
    return { isDarkTheme, theme, changeTheme }
  },
  {
    persist: {
      storage: localStorage,//本地存储
      paths: ["isDarkTheme"],//将isDarkTheme持久化保存
    },
  })
