package com.suzhou.concept.utils

/**
苏州爱语吧科技有限公司
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RequestConverter(val format: ConverterFormat = ConverterFormat.JSON)
