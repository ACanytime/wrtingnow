<template>
    <div>
        <!-- 搜索输入框 -->
        <div class="search-container">
            <n-input-group>
                <n-input v-model:value="search" placeholder="搜索"></n-input>
                <n-button @click="getRecordList()">搜索</n-button>
            </n-input-group>
        </div>

        <n-table :single-line="false" size="small">
            <thead>
                <tr>
                    <th>最近操作事件</th>
                    <th>最近操作时间</th>
                    <th>操作事件码</th>
                    <th>用户编号</th>
                    <th>笔记编号</th>
                    <th>小记编号</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(item, index) in recordList" :key="index">
                    <td>{{ item.desc }}</td>
                    <td>{{ item.time }}</td>
                    <td>{{ item.event }}</td>
                    <td>{{ item.userId }}</td>
                    <td>{{ item.noteId }}</td>
                    <td>{{ item.thingId }}</td>
                </tr>
            </tbody>
        </n-table>
    </div>
</template>
  
<style>
/* 设置搜索框在上方 */
.search-container {
    margin-bottom: 10px;
}

/* 设置表格宽度为 100% */
.n-table {
    width: 280%;
}
</style>
  

<script setup>
import { ref } from "vue"
import { getUserToken, loginInvalid } from "@/utils/UserLoginUtil"
import { useMessage, useLoadingBar } from 'naive-ui'
import { noteBaseRequest } from "@/request/note_request"
//消息对象
const message = useMessage()
//加载条对象
const loadingBar = useLoadingBar()
//搜索对象
const search = ref(null)
//最近操作对象
const recordList = ref([])
const getRecordList = async () => {
    //判断用户的登录状态
    const userToken = await getUserToken()

    loadingBar.start()//加载条开始
    //发送获取笔记列表请求
    const { data: responseData } = await noteBaseRequest.get(
        "/record/list",
        {
            params: {
                search: search.value,
            },
            headers: { userToken }
        }
    ).catch(() => {
        loadingBar.error()//加载条异常结束
        //发送请求失败（404，500,400...）
        throw message.error("获取最近操作列表请求失败")//显示发送请求失败的通知
    })
    //得到服务器返回的数据，进行处理
    console.log(responseData)

    if (responseData.success) {
        //封装笔记列表
        loadingBar.finish()//加载条结束
        recordList.value = responseData.data
        console.log(recordList)

    }
    else {
        loadingBar.error()//加载条异常结束
        message.error(responseData.message)//显示发送请求失败的通知
        if (responseData.code === "L_008") {
            loginInvalid(true)//登录失效
        }
    }
}
getRecordList()
</script>

