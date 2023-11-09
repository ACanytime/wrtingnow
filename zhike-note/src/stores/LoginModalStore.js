
import { defineStore } from 'pinia'
import { ref } from 'vue'
//关于登录模态框的全局状态
export const useLoginModalStore = defineStore(
  "login-modal",
  () => {
    //是否显示登录模态框 
    const showLoginModal = ref(false)

    // 改变登录状态框的显示状态
    const changeLoginModalShowStatus = (show) => {
      showLoginModal.value = show
    }

    return { showLoginModal, changeLoginModalShowStatus }
  }
)
