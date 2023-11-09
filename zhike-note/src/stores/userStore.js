
import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'

export const useUserStore = defineStore(
  "user",
  () => {
    const token = ref(null)//用户登录的token值
    const id = ref(null)//编号
    const email = ref(null)//邮箱
    const nickname = ref(null)//昵称
    const headPic = ref(null)//头像
    const level = ref(null)//等级
    const time = ref(null)//注册时间

    //设置用户显示的头像
    const head_image = computed(() => {
      //如果用户暂无头像，则使用默认头像，否则使用自己头像
      if (headPic.value === null || headPic.value === undefined) {
        return "https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg"
      }
      else return headPic.value
    })
    //设置用户显示的昵称
    const nickName = computed(() => {
      //如果用户暂无昵称，则使用默认昵称，否则使用自己昵称
      if (nickname.value === null || nickname.value === undefined) {
        return "暂无设定昵称"
      }
      else return nickname.value
    })
    //设置用户显示的等级
    const levelInfo = computed(() => {
      //如果用户等级为0，则是会员，否则则是超级会员
      if (level.value === 0) {
        return "会员"
      }
      else return "超级会员"
    })

    //设置用户信息函数
    const setUserInfo = (u_token, u_id, u_email, u_nickname, u_headPic, u_level, u_time) => {
      token.value = u_token
      id.value = u_id
      email.value = u_email
      nickname.value = u_nickname
      headPic.value = u_headPic
      level.value = u_level
      time.value = u_time
    }
    //重置用户信息
    const resetUserInfo = () => {
      token.value = null
    }
    watch(
      () => token.value,
      newData => {
        if (newData === null) {
          id.value = null
          email.value = null
          nickname.value = null
          headPic.value = null
          level.value = null
          time.value = null

        }
      }
    )
    return { token, id, email, nickname, headPic, level, time, head_image, nickName, levelInfo, setUserInfo, resetUserInfo }
  },
  {
    persist: {
      storage: localStorage,//本地存储
    },
  }
)
