/**
 * 禁用按钮/取消禁用按钮
 * @param {*} btnDisabled  按钮disabled 属性绑定的数据源
 * @param {Boolean} isDisabled 是否是禁用按钮
 * @param {Boolean} isDelay  取消禁用按钮是否需要延迟
 * @param {Number} time 延迟的时间(秒)
 */
export const disabledBtn = (btnDisabled, isDisabled, isDelay = false, time = 1000) => {
    if (isDisabled) {
        btnDisabled.value = isDisabled//禁用按钮
    }
    else {
        if (isDelay) {
            //需要有延迟取消禁用按钮
            setTimeout(() => {
                btnDisabled.value = isDisabled//取消禁用按钮
            }, time * 1000)
        } else {
            btnDisabled.value = isDisabled//取消禁用按钮
        }

    }
}