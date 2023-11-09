import { useLoginModalStore } from "@/stores/loginModalStore"
import { useUserStore } from "@/stores/userStore"

/**
 * 获取本地存储中的userToken的值，如果没有则显示登录的窗口
 * @returns 
 */
export const getUserToken = async () => {
    const { token } = useUserStore()//用户store对象中的重置用户信息的函数
    if (token === null) {
        //未登录
        const { changeLoginModalShowStatus } = useLoginModalStore()//改变登录窗口显示的函数

        await changeLoginModalShowStatus(true)//弹出登录窗口（结束）
        throw "未登录"
    }
    else {
        return token
    }
}
//登录失效
export const loginInvalid = show => {
    const { resetUserInfo } = useUserStore()
    resetUserInfo()//重置用户的登录状态
    if (show) {
        //未登录
        const { changeLoginModalShowStatus } = useLoginModalStore()//改变登录窗口显示的函数
        changeLoginModalShowStatus(true)//弹出登录窗口（结束）
    }
}