<template>
    <n-space vertical>
        <!-- 添加文件图标 -->
        <n-dropdown :options="addBoxOptions" placement="right-start">
            <n-button text type="primary">
                <n-icon size="34" :component="AddBoxRound">
                </n-icon>
            </n-button>
        </n-dropdown>
        <!-- 搜索文件图标 -->
        <n-button text type="primary">
            <n-icon size="26" :component="SearchRound">
            </n-icon>
        </n-button>
    </n-space>
    <!-- 分割线 -->
    <n-divider style="widows: 34px; margin: 15px auto;"></n-divider>
    <n-space vertical :size="16">
        <n-popover v-for=" menu  in  mainMenus " :key="menu.label" placement="right" :show-arrow="false">
            <template #trigger>
                <n-button style="width: 34px;padding: 0;" :quaternary="!isHighlightMenu(menu.to)"
                    :type="isHighlightMenu(menu.to) ? 'primary' : 'default'" :tertiary="isHighlightMenu(menu.to)"
                    @click="router.push(menu.to)">
                    <n-icon :size="menu.icon_size" :component="menu.icon">
                    </n-icon>
                </n-button>
            </template>
            <span>{{ menu.label }}</span>
        </n-popover>
    </n-space>
</template>

<script setup>
import { AddBoxRound, DeleteOutlineRound, StickyNote2Outlined, EventNoteRound, SearchRound, AccessTimeRound, StarBorderRound, ShoppingBagOutlined } from '@vicons/material'
import { NIcon } from 'naive-ui';
import { h, watch, ref, inject } from 'vue'
import { useRouter } from 'vue-router';
import bus from 'vue3-eventbus'


const router = useRouter()//路由对象
const routerPath = inject('routerPath')


//读图标，在菜单里面要以函数的形式来读取图标
const renderIcon = (icon, size, color) => {
    return () => h(NIcon, { size, color }, { default: () => h(icon) })
}
//新增笔记小记菜单选项
const addBoxOptions = [{
    label: "新增笔记",
    key: "note",
    icon: renderIcon(StickyNote2Outlined, 22, "#18a058"),
    props: {
        onClick: () => {
            //跳转至路由为/note的地址
            router.push("/note")

        }
    }
},
{
    label: "新增小记",
    key: "thing",
    icon: renderIcon(EventNoteRound, 22, "#2080f0"),
    props: {
        onClick: () => {
            //跳转至路由为/thing的地址
            router.push("/thing").then(() => {
                bus.emit('newCreateThing')
            })
        }
    }
},
{
    label: "最近操作",
    key: "record",
    icon: renderIcon(AccessTimeRound, 22, "#2080f0"),
    props: {
        onClick: () => {
            //跳转至路由为/record的地址
            router.push("/record")

        }
    }
}
]

//主要的菜单
const mainMenus = [
    {
        label: "最近操作",//弹出信息
        icon: AccessTimeRound,//图标
        icon_size: 24,//大小
        to: '/record'//路由
    },
    {
        label: "笔记",
        icon: StickyNote2Outlined,
        icon_size: 24,
        to: '/note'
    },
    {
        label: "小记",
        icon: EventNoteRound,
        icon_size: 24,
        to: '/thing'
    },
    {
        label: "收藏",
        icon: StarBorderRound,
        icon_size: 28,
        to: ''
    },
    {
        label: "商城",
        icon: ShoppingBagOutlined,
        icon_size: 24,
        to: ''
    },
    {
        label: "回收站",
        icon: DeleteOutlineRound,
        icon_size: 24,
        to: '/recycle'
    },
]
/**
 * 是否高亮显示菜单按钮
 * @param toRouterPath {String} 菜单跳转的路由地址
 */
const isHighlightMenu = (toRouterPath) => {
    if (!toRouterPath) return false
    return routerPath.value.startsWith(toRouterPath)//判断路由地址是否以.....为开头
}
</script>

<style></style>