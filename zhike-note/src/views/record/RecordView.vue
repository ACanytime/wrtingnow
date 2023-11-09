<template>
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
            <tr>
                <td>放弃</td>
                <td>反常的</td>
                <td>彻底废除</td>
                <td>...</td>
                <td>干！我刚才背的是啥</td>
            </tr>
            <tr>
                <td>...</td>
                <td>...</td>
                <td>...</td>
                <td>...</td>
                <td>...</td>
            </tr>
        </tbody>
    </n-table>
</template>

<script setup></script>

<style>
/* 设置表格宽度为 80% */
.n-table {
    width: 100%;
    height: 100px;
    /* 居中显示 */
}
</style>
