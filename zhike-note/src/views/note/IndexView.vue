<template>
  <!-- 笔记页面容器 -->
  <n-layout has-sider>
    <!-- 笔记列表容器（可收缩的）左侧 -->
    <n-layout-sider bordered show-trigger :collapsed-width="0" :width="340">

      <!-- 滚动条 -->
      <n-scrollbar @scroll="contextMenu.show = false">
        <!-- 标题区域，新增笔记列表 -->
        <n-card :bordered="false" style="position: sticky;top: 0;z-index: 1; width: calc(100%-1px)">
          <template #action>
            <n-space align="center" justify="space-between">
              <h3 style="margin: 0;">笔记列表</h3>
              <n-button circle type="primary" @click="createNote">
                <n-icon :size="22" :component="PlusRound"></n-icon>
              </n-button>
            </n-space>
          </template>
        </n-card>

        <n-space v-if="loading" vertical style="margin: 12px;">
          <n-card size="small" v-for="n in 3" :key="n">
            <n-space vertical>
              <!-- 笔记列表的骨架屏 -->
              <n-skeleton :height="26" :width="120"></n-skeleton>
              <n-skeleton text :repeat="2"></n-skeleton>
              <n-skeleton :height="23" :width="200"></n-skeleton>
            </n-space>
          </n-card>
        </n-space>
        <!-- 笔记列表 -->
        <n-list hoverable clickable style="margin: 5px;">
          <TransitionGroup @before-enter="beforeEnter" @enter="enterEvent" @before-leave="beforeLeave" @leave="LeaveEvent"
            move-class="move-transition">
            <template v-if="!loading && noteList.length > 0">
              <n-list-item @click="goEditNoteView(n.id)" v-for="(n, index) in noteList" :key="n.id" :data-index="index"
                @contextmenu="showContextMenu($event, n.id, !!n.top, n.title)"
                :class="{ 'contexting': contextMenu.id === n.id && contextMenu.show, 'editing': editingNoteId === n.id }">
                <NoteCard :id="n.id" :title="n.title ? n.title : defaultTitle" :desc="n.body" :top="!!n.top"
                  :time="n.updatetime"></NoteCard>
              </n-list-item>
            </template>

          </TransitionGroup>
        </n-list>
        <!-- 暂无笔记列表的描述 -->
        <n-empty v-if="!loading && noteList.length === 0" style="margin:80px auto; width: max-content;" size="huge"
          description="暂无小记列表，快快创建">
          <template #icon>
            <n-icon :component="SubtitlesOffOutlined"></n-icon>
          </template>
          <template #extra>
            <n-button dashed>创建笔记</n-button>
          </template>
        </n-empty>
      </n-scrollbar>
    </n-layout-sider>
    <!-- 笔记编辑容器 -->
    <n-layout-content embedded content-style="padding:20px">
      <router-view></router-view>
    </n-layout-content>
  </n-layout>
  <!-- 删除提醒框 -->
  <DeleteRemindDialog :title="contextMenu.title" :show="displaydelete" @delete="toDeleteThing"
    @cancel="displaydelete = false">
  </DeleteRemindDialog>
  <!-- 右键菜单 -->
  <n-dropdown placement="bottom-start" trigger="manual" :x="contextMenu.x" :y="contextMenu.y"
    :options="contextMenu.options" :show="contextMenu.show" :on-clickoutside="clickContextMenuOutside"
    @select="selectContextMenu">
  </n-dropdown>
</template>
<script setup>
import { ref, h, computed, inject } from 'vue'
import { PlusRound, SubtitlesOffOutlined, DriveFileRenameOutlineOutlined, DeleteOutlineRound, ArrowCircleUpRound, ArrowCircleDownRound } from '@vicons/material';
import NoteCard from "@/components/note/NoteCard.vue"
import { noteBaseRequest } from "@/request/note_request"
import { getUserToken, loginInvalid } from "@/utils/UserLoginUtil"
import { useMessage, useLoadingBar } from 'naive-ui'
import gsap from 'gsap'
import { NIcon } from 'naive-ui';
import DeleteRemindDialog from '../../components/remind/DeleteRemindDialog.vue'
import { useRouter } from 'vue-router';
//消息对象
const message = useMessage()
//加载条对象
const loadingBar = useLoadingBar()

const router = useRouter()//路由对象
const routerPath = inject('routerPath')

const editingNoteId = computed(() => {
  const index = routerPath.value.indexOf('/note/edit/');
  if (index === -1) return null;
  return parseInt(routerPath.value.substring('/note/edit/'.length))

})

/**
 * 前往编辑笔记的视图
 * @param {Number} id 笔记编号
 */
const goEditNoteView = (id) => {
  if (id !== null)
    router.push('/note/edit/' + id) //跳转路由
  else message.error("前往编辑页面失败")
}

//笔记列表
const noteList = ref([])

//是否处于加载状态
const loading = ref(true)

//是否显示需要延迟动画
let enterDelay = true
//隐藏是否需要动画
let hiddenAnimation = true

//执行显示动画之前的初始位置
const beforeEnter = (el) => {
  gsap.set(el, {
    x: 30,
    opacity: 0
  })
}
//执行显示动画
const enterEvent = (el, done) => {
  gsap.to(el, {
    x: 0,//偏移量
    opacity: 1,//透明度
    duration: 0.5,//单位是秒
    delay: () => (enterDelay.value ? 0 : el.dataset.index * 0.12),//延迟动画，依次出现
    onComplete: done//动画执行后调用的函数
  })
}
//执行隐藏动画之前的初始位置
const beforeLeave = (el) => {
  if (hiddenAnimation) {
    //获取删除的元素距离父组件的左和上位置
    const left = el.offsetLeft
    const top = el.offsetTop
    //设置删除组件的属性(需要脱离文档里)
    gsap.set(el, {
      boxShadow: '0 0 5px black',
      zIndex: 1,
      position: 'absolute',
      top,
      left,
      backdropFilter: 'blur(5px)',
      width: 'calc(100%-24px)'
    })
  }
}
//执行隐藏动画
const LeaveEvent = (el, done) => {
  if (hiddenAnimation) {
    gsap.to(el, {
      scale: 0,//缩放
      duration: 0.5,//动画时间（秒）
      onComplete: done //动画执行完毕后调用的函数
    })
  }
  else {
    gsap.to(el, {
      duration: 0,//单位是秒
      onComplete: done//动画执行后调用的函数
    })
  }
}
//获取小记列表
/**
 * @param ed{Boolean} 显示是否需要延迟动画
 * @param {Boolean} ha 隐藏是否需要动画
 * @param {Boolean} newCreate 是否是新增小记
 */
const getNoteThing = async (ed, ha) => {
  enterDelay = ed//显示是否需要延迟动画
  hiddenAnimation = ha //隐藏是否需要动画
  //判断用户的登录状态
  const userToken = await getUserToken()

  loadingBar.start()//加载条开始
  //发送获取笔记列表请求
  const { data: responseData } = await noteBaseRequest.get(
    "/note/list",
    {

      headers: { userToken }
    }
  ).catch(() => {
    loadingBar.error()//加载条异常结束
    //发送请求失败（404，500,400...）
    throw message.error("获取笔记列表请求失败")//显示发送请求失败的通知
  })
  //得到服务器返回的数据，进行处理
  console.log(responseData)

  if (responseData.success) {
    //封装笔记列表
    loadingBar.finish()//加载条结束
    loading.value = false//加载状态已结束，骨架屏消失
    noteList.value = responseData.data

  }
  else {
    loadingBar.error()//加载条异常结束
    message.error(responseData.message)//显示发送请求失败的通知
    if (responseData.code === "L_008") {
      loginInvalid(true)//登录失效
    }
  }
}
getNoteThing(true, true)
//读图标
const renderIcon = (icon) => {
  return () => {
    return h(NIcon, null, {
      default: () => h(icon)
    });
  };
};
//右键菜单对象
const contextMenu = ref({
  show: false,//是否显示右键菜单
  x: 0,//x坐标
  y: 0,//y坐标
  id: null,//笔记编号
  title: '',//笔记标题
  top: false,//笔记是否置顶
  options: computed(() => {
    return [
      {
        label: "重命名",
        key: "rename",
        icon: renderIcon(DriveFileRenameOutlineOutlined)
      },
      {
        label: "删除",
        key: "delete",
        icon: renderIcon(DeleteOutlineRound)
      },
      {
        label: "取消置顶",
        key: "cancel-top",
        icon: renderIcon(ArrowCircleUpRound),
        show: contextMenu.value.top
      },
      {
        label: "置顶",
        key: "top",
        icon: renderIcon(ArrowCircleDownRound),
        show: !contextMenu.value.top
      },
    ]
  })
})
//展示右键菜单
const showContextMenu = (e, id, top, title) => {
  e.preventDefault()
  contextMenu.value.show = false
  nextTick().then(() => {
    contextMenu.value.show = true
    contextMenu.value.x = e.clientX
    contextMenu.value.y = e.clientY
    contextMenu.value.id = id
    contextMenu.value.top = top
    contextMenu.value.title = title ? title : defaultTitle
  })
}
//点击右键菜单的外界关闭菜单
const clickContextMenuOutside = () => {
  contextMenu.value.show = false
}

//选择了右键菜单选项
const selectContextMenu = (key) => {
  contextMenu.value.show = false//关闭右键菜单
  if (key === 'cancel-top') {//取消置顶
    topNote(false)
  } else if (key === 'top') {//置顶
    topNote(true)
  } else if (key === 'delete') {//删除
    displaydelete.value = true
  }
}
/**
 * 置顶小记
 * @param {Boolean} isTop 是否置顶
 * @param {Number} noteId 笔记编号
 */
const topNote = async isTop => {
  //判断用户的登录状态
  const userToken = await getUserToken()

  loadingBar.start()//加载条开始
  //发送置顶笔记请求
  const { data: responseData } = await noteBaseRequest.get(
    "/note/top",
    {
      params: { isTop, noteId: contextMenu.value.id },
      headers: { userToken }
    }
  ).catch(() => {
    loadingBar.error()//加载条异常结束
    //发送请求失败（404，500,400...）
    throw message.error(isTop ? "置顶笔记请求失败" : "取消置顶笔记请求失败")//显示发送请求失败的通知
  })
  //得到服务器返回的数据，进行处理
  console.log(responseData)

  if (responseData.success) {
    loadingBar.finish()//加载条结束
    message.success(responseData.message)//显示发送请求成功的通知
    getNoteThing(false, false)//置顶后重新获取笔记列表
  }
  else {
    loadingBar.error()//加载条异常结束
    message.error(responseData.message)//显示发送请求失败的通知
    if (responseData.code === "L_008") {
      loginInvalid(true)//登录失效
    }
  }
}

//是否显示删除提醒框
const displaydelete = ref(false)
/**
 * 删除笔记
 * @param {*} complete 是否彻底删除 
 */
const toDeleteThing = async (complete) => {
  displaydelete.value = false//关闭提醒框
  //判断用户的登录状态
  const userToken = await getUserToken()
  loadingBar.start()//加载条开始

  //发送删除小记请求
  const { data: responseData } = await noteBaseRequest.delete(
    "/note/delete",
    {
      params: {
        complete, noteId: contextMenu.value.id,
        isRecycleBin: false
      },
      headers: { userToken }
    }
  ).catch(() => {
    loadingBar.error()//加载条异常结束
    //发送请求失败（404，500,400...）
    throw message.error(complete ? "彻底删除请求失败" : "删除请求失败")//显示发送请求失败的通知
  })
  //得到服务器返回的数据，进行处理
  console.log(responseData)

  if (responseData.success) {
    loadingBar.finish()//加载条结束
    message.success(responseData.message)//显示发送请求成功的通知
    getNoteThing(false, true)//重新获取小记列表
  }
  else {
    loadingBar.error()//加载条异常结束
    message.error(responseData.message)//显示发送请求失败的通知
    if (responseData.code === "L_008") {
      loginInvalid(true)//登录失效
    }
  }
}
//---------------笔记创建操作--------------

let defaultTitle = '暂未设置标题'

/**
 * 创建笔记
 */
const createNote = async () => {
  //判断用户的登录状态
  const userToken = await getUserToken()
  loadingBar.start()//加载条开始

  //发送删除小记请求
  const { data: responseData } = await noteBaseRequest.put(
    "/note/create",
    {},

    {
      headers: { userToken }
    }
  ).catch(() => {
    loadingBar.error()//加载条异常结束
    //发送请求失败（404，500,400...）
    throw message.error("创建请求失败")//显示发送请求失败的通知
  })
  //得到服务器返回的数据，进行处理
  console.log(responseData)

  if (responseData.success) {
    loadingBar.finish()//加载条结束
    message.success(responseData.message)//显示发送请求成功的通知
    goEditNoteView(responseData.data)

    getNoteThing(false, false)//重新获取小记列表(新增的笔记不需要延迟显示效果)
  }
  else {
    loadingBar.error()//加载条异常结束
    message.error(responseData.message)//显示发送请求失败的通知
    if (responseData.code === "L_008") {
      loginInvalid(true)//登录失效
    }
  }
}

</script>
<style scoped>
.move-transition {
  /* 取消置顶和置顶时有动画 */
  transition: all 0.5s;
}

.contexting {
  box-shadow: 0 0 3px rgba(114, 229, 190);
}

.editing {
  box-shadow: 0 0 3px rgb(252, 36, 3);
}
</style>