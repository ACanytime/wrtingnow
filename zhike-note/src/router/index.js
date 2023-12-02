import { createRouter, createWebHistory } from 'vue-router'
import ThingIndexView from '@/views/thing/IndexView.vue';
import RecordView from '@/views/record/RecordView.vue';
import RecycleView from '@/views/recycle/RecycleView.vue'
import Home from '@/views/home/Home.vue';
import RecycleThingView from "@/views/recycle/RecycleThingView.vue"
const routes = [

  //首页
  {
    path: '/',
    component: Home
  },
  //回收站
  {
    path: '/recycle',
    component: RecycleView
  },
  {
    path: '/Thingrecycle',
    component: RecycleThingView
  },
  //小记界面的路由链接
  {
    path: '/thing',
    component: ThingIndexView
  },
  //笔记的路由地址
  {
    path: '/note',
    component: () => import("@/views/note/IndexView.vue"),
    children: [
      {
        path: 'edit/:id',
        props: true,
        component: () => import("@/views/note/EditNote.vue")
      }
    ]
  },
  //最近操作的路由地址
  {
    path: '/record',
    component: RecordView
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
