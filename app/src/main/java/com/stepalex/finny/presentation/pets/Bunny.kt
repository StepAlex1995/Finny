package com.stepalex.finny.presentation.pets

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class PetStage {
    Baby,
    Teenager,
    Adult
}

private val OutlineColor = Color.Black
private val FillColor = Color.White
private val BlushColor = Color(0xFFFEE1E1)

@Composable
fun Bunny(stage: PetStage, modifier: Modifier = Modifier) {
    // Создаем транзишн, который следит за изменением состояния stage
    val transition = updateTransition(targetState = stage, label = "BunnyStageTransition")
    // Анимируем коэффициент масштаба в зависимости от целевой стадии
    val stageScale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "BunnyScale"
    ) { targetStage ->
        when (targetStage) {
            PetStage.Baby -> 0.6f
            PetStage.Teenager -> 0.75f
            PetStage.Adult -> 0.85f
        }
    }
    // Анимация прогресса для лапок малыша
    val babyFeetProgress by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "BabyFeetProgress"
    ) { targetStage ->
        if (targetStage == PetStage.Baby) 1f else 0f
    }
    // Анимируем верхнюю границу туловища (bodyMainPath top)
    val bodyTop by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BodyTop"
    ) { targetStage ->
        when (targetStage) {
            PetStage.Baby -> 0.87f     // Прячется за головой малыша
            PetStage.Teenager -> 0.50f // Позиция подростка
            PetStage.Adult -> 0.70f    // Позиция взрослого
        }
    }
    // Анимируем нижнюю границу туловища (bodyMainPath bottom)
    val bodyBottom by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BodyBottom"
    ) { targetStage ->
        when (targetStage) {
            PetStage.Baby -> 0.87f     // Схлопнуто в ноль
            PetStage.Teenager -> 0.99f // Позиция подростка
            PetStage.Adult -> 1.19f    // Позиция взрослого
        }
    }
    // Анимируем нижнюю границу выступов нижних лапок
    val bumpBottom by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BumpBottom"
    ) { targetStage ->
        when (targetStage) {
            PetStage.Baby -> 0.87f
            PetStage.Teenager -> 1.04f
            PetStage.Adult -> 1.24f
        }
    }
    // Анимируем верхнюю границу выступов нижних лапок (они привязаны к низу тела)
    val bumpTop by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BumpTop"
    ) { targetStage ->
        when (targetStage) {
            PetStage.Baby -> 0.87f
            PetStage.Teenager -> 0.65f
            PetStage.Adult -> 1.05f
        }
    }
    // Прогресс появления хвостика и рук (0f у малыша, 1f у остальных)
    val bodyElementsProgress by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BodyElementsProgress"
    ) { targetStage ->
        if (targetStage == PetStage.Baby) 0f else 1f
    }
    // Анимация позиции хвостика (сдвигается между Teenager и Adult)
    val tailLeft by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "TailLeft"
    ) { targetStage ->
        if (targetStage == PetStage.Adult) 0.80f else 0.76f
    }
    val tailTop by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "TailTop"
    ) { targetStage ->
        if (targetStage == PetStage.Adult) 0.93f else 0.80f
    }
    val tailRight by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "TailRight"
    ) { targetStage ->
        if (targetStage == PetStage.Adult) 0.94f else 0.90f
    }
    val tailBottom by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "TailBottom"
    ) { targetStage ->
        if (targetStage == PetStage.Adult) 1.07f else 0.94f
    }

    // Координаты рук (плавно интерполируем top и bottom между стадиями)
    val armTop by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "ArmTop"
    ) { targetStage ->
        if (targetStage == PetStage.Adult) 0.85f else 0.775f
    }
    val armBottom by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "ArmBottom"
    ) { targetStage ->
        if (targetStage == PetStage.Adult) 0.97f else 0.87f
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .aspectRatio(1f)
    ) {
        val w = size.width
        val h = size.height

        // Общие стили линий
        val strokeWidth = w * 0.045f
        val strokeStyle = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        val mouthStrokeStyle = Stroke(
            width = strokeWidth * 0.7f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // Применяем масштабирование относительно центра холста, чтобы уменьшать/увеличивать питомца целиком
        withTransform({
            scale(scaleX = stageScale, scaleY = stageScale, pivot = Offset(w / 2f, h / 2f))
        }) {
            //поднимаем отрисовку выше, чтоб потом дорисовать туловище
            withTransform({
                translate(top = -h * 0.14f)
            }) {
                // Отрисовка базовой анатомии кролика
                drawBunnyBodyAndEars(
                    w = w,
                    h = h,
                    strokeStyle = strokeStyle,
                    bodyTop = bodyTop,
                    bodyBottom = bodyBottom,
                    bumpTop = bumpTop,
                    bumpBottom = bumpBottom,
                    bodyElementsProgress = bodyElementsProgress,
                    tailLeft = tailLeft,
                    tailTop = tailTop,
                    tailRight = tailRight,
                    tailBottom = tailBottom,
                    armTop = armTop,
                    armBottom = armBottom
                )
                // Отрисовка элементов мордочки
                drawBunnyFace(w, h, mouthStrokeStyle)
                if (babyFeetProgress > 0f) {
                    drawBunnyFeet(w, h, strokeStyle, babyFeetProgress)
                }
            }

        }
    }
}

/**
 * Сложная геометрия: построение головы, ушек (внешних и внутренних) и их объединение.
 */
private fun DrawScope.drawBunnyBodyAndEars(
    w: Float,
    h: Float,
    strokeStyle: Stroke,
    bodyTop: Float,
    bodyBottom: Float,
    bumpTop: Float,
    bumpBottom: Float,
    bodyElementsProgress: Float,
    tailLeft: Float, tailTop: Float, tailRight: Float, tailBottom: Float,
    armTop: Float, armBottom: Float
) {
    // Голова
    val headPath = Path().apply {
        addRoundRect(
            RoundRect(
                left = w * 0.08f,
                top = h * 0.31f,
                right = w * 0.92f,
                bottom = h * 0.87f,
                cornerRadius = CornerRadius(w * 0.36f, h * 0.28f)
            )
        )
    }

    val leftInnerEarPath = Path()
    val rightInnerEarPath = Path()

    val leftEarPath = Path().apply {
        addRoundRect(
            RoundRect(
                topLeftCornerRadius = CornerRadius(w * 0.12f, h * 0.20f),
                topRightCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                bottomLeftCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                bottomRightCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                left = w * 0.24f,
                top = h * 0.05f,
                right = w * 0.49f,
                bottom = h * 0.55f
            )
        )
        leftInnerEarPath.addRoundRect(
            RoundRect(
                topLeftCornerRadius = CornerRadius(w * 0.20f, h * 0.25f),
                topRightCornerRadius = CornerRadius(w * 0.20f, h * 0.25f),
                bottomLeftCornerRadius = CornerRadius(w * 0.075f, h * 0.15f),
                bottomRightCornerRadius = CornerRadius(w * 0.2f, h * 0.2f),
                left = w * 0.29f,
                top = h * 0.11f,
                right = w * 0.44f,
                bottom = h * 0.35f
            )
        )
        val pivotX = w * 0.355f
        val pivotY = h * 0.45f
        val matrix = Matrix().apply {
            translate(pivotX, pivotY)
            rotateZ(-8f)
            translate(-pivotX, -pivotY)
        }
        transform(matrix)
        leftInnerEarPath.transform(matrix)
    }

    val rightEarPath = Path().apply {
        addRoundRect(
            RoundRect(
                topLeftCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                topRightCornerRadius = CornerRadius(w * 0.12f, h * 0.20f),
                bottomLeftCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                bottomRightCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                left = w * 0.51f,
                top = h * 0.05f,
                right = w * 0.76f,
                bottom = h * 0.55f
            )
        )
        rightInnerEarPath.addRoundRect(
            RoundRect(
                topLeftCornerRadius = CornerRadius(w * 0.20f, h * 0.25f),
                topRightCornerRadius = CornerRadius(w * 0.20f, h * 0.25f),
                bottomLeftCornerRadius = CornerRadius(w * 0.2f, h * 0.2f),
                bottomRightCornerRadius = CornerRadius(w * 0.075f, h * 0.15f),
                left = w * 0.56f,
                top = h * 0.11f,
                right = w * 0.71f,
                bottom = h * 0.35f
            )
        )
        val pivotX = w * 0.645f
        val pivotY = h * 0.45f
        val matrix = Matrix().apply {
            translate(pivotX, pivotY)
            rotateZ(8f)
            translate(-pivotX, -pivotY)
        }
        transform(matrix)
        rightInnerEarPath.transform(matrix)
    }
    // Хвостик (круглый скругленный прямоугольник, подкладывается под правый нижний бок)
    if (bodyElementsProgress > 0f) {
        val tailPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = w * tailLeft,
                    top = h * tailTop,
                    right = w * tailRight,
                    bottom = h * tailBottom,
                    cornerRadius = CornerRadius(w * 0.065f, h * 0.065f)
                )
            )
        }
        drawPath(path = tailPath, color = FillColor, alpha = bodyElementsProgress)
        drawPath(
            path = tailPath,
            color = OutlineColor,
            style = strokeStyle,
            alpha = bodyElementsProgress
        )
    }

    // --- Анимированная геометрия туловища ---
    val bodyMainPath = Path().apply {
        addRoundRect(
            RoundRect(
                left = w * 0.15f,
                top = h * bodyTop,
                right = w * 0.85f,
                bottom = h * bodyBottom,
                topLeftCornerRadius = CornerRadius(w * 0.38f, h * 0.32f),
                topRightCornerRadius = CornerRadius(w * 0.38f, h * 0.32f),
                bottomLeftCornerRadius = CornerRadius(w * 0.2f, h * 0.2f),
                bottomRightCornerRadius = CornerRadius(w * 0.2f, h * 0.2f),
            )
        )
    }

    val bottomLeftBump = Path().apply {
        addRoundRect(
            RoundRect(
                left = w * 0.23f,
                top = h * bumpTop,
                right = w * 0.42f,
                bottom = h * bumpBottom,
                cornerRadius = CornerRadius(w * 0.71f, w * 0.71f)
            )
        )
    }
    val bottomRightBump = Path().apply {
        addRoundRect(
            RoundRect(
                left = w * 0.6f,
                top = h * bumpTop,
                right = w * 0.78f,
                bottom = h * bumpBottom,
                cornerRadius = CornerRadius(w * 0.71f, w * 0.71f)
            )
        )
    }

    // Объединяем всё в единый плавный силуэт
    val fullBunnyPath = Path().apply {
        op(headPath, leftEarPath, PathOperation.Union)
        op(this, rightEarPath, PathOperation.Union)
        op(this, bodyMainPath, PathOperation.Union)
        op(this, bottomLeftBump, PathOperation.Union)
        op(this, bottomRightBump, PathOperation.Union)
    }
    // Отрисовка силуэта
    drawPath(path = fullBunnyPath, color = FillColor)
    drawPath(path = fullBunnyPath, color = OutlineColor, style = strokeStyle)

    // --- Передние лапки-ручки (плавно прорисовываются поверх пузика) ---
    if (bodyElementsProgress > 0f) {
        val leftArmPath = Path().apply {
            arcTo(
                rect = Rect(
                    left = w * 0.30f,
                    top = h * armTop,
                    right = w * 0.45f,
                    bottom = h * armBottom
                ), startAngleDegrees = -100f, sweepAngleDegrees = 200f, forceMoveTo = true
            )
            val pivotX = w * 0.35f
            val pivotY = h * 0.91f
            val matrix = Matrix().apply {
                translate(
                    pivotX,
                    pivotY
                )
                rotateZ(-30f)
                translate(-pivotX, -pivotY)
            }
            transform(matrix)
        }
        val rightArmPath = Path().apply {
            arcTo(
                rect = Rect(
                    left = w * 0.55f,
                    top = h * armTop,
                    right = w * 0.70f,
                    bottom = h * armBottom
                ), startAngleDegrees = 80f, sweepAngleDegrees = 200f, forceMoveTo = true
            )
            val pivotX = w * 0.65f
            val pivotY = h * 0.91f
            val matrix = Matrix().apply {
                translate(
                    pivotX,
                    pivotY
                )
                rotateZ(30f)
                translate(-pivotX, -pivotY)
            }
            transform(matrix)
        }

        drawPath(
            path = leftArmPath,
            color = OutlineColor,
            style = strokeStyle,
            alpha = bodyElementsProgress
        )
        drawPath(
            path = rightArmPath,
            color = OutlineColor,
            style = strokeStyle,
            alpha = bodyElementsProgress
        )
    }

    // Розовые серединки ушей
    drawPath(path = leftInnerEarPath, color = BlushColor)
    drawPath(path = rightInnerEarPath, color = BlushColor)

}

/**
 * Отрисовка лица: щечки, глаза, ротик.
 */
private fun DrawScope.drawBunnyFace(w: Float, h: Float, mouthStrokeStyle: Stroke) {
    // Щечки
    drawOval(
        color = BlushColor,
        topLeft = Offset(w * 0.17f, h * 0.60f),
        size = Size(w * 0.16f, h * 0.13f)
    )
    drawOval(
        color = BlushColor,
        topLeft = Offset(w * 0.67f, h * 0.60f),
        size = Size(w * 0.16f, h * 0.13f)
    )

    // Глаза
    drawCircle(color = OutlineColor, radius = w * 0.045f, center = Offset(w * 0.34f, h * 0.63f))
    drawCircle(color = OutlineColor, radius = w * 0.045f, center = Offset(w * 0.66f, h * 0.63f))

    // Рот
    val mouthPath = Path().apply {
        arcTo(
            rect = Rect(
                left = w * 0.435f,
                top = h * 0.64f,
                right = w * 0.50f,
                bottom = h * 0.69f
            ),
            startAngleDegrees = 180f, sweepAngleDegrees = -180f, forceMoveTo = true
        )
        arcTo(
            rect = Rect(
                left = w * 0.50f,
                top = h * 0.64f,
                right = w * 0.565f,
                bottom = h * 0.69f
            ),
            startAngleDegrees = 180f, sweepAngleDegrees = -180f, forceMoveTo = false
        )
    }
    drawPath(path = mouthPath, color = OutlineColor, style = mouthStrokeStyle)
}

/**
 * Отрисовка лапок поверх готового тела.
 */
private fun DrawScope.drawBunnyFeet(w: Float, h: Float, strokeStyle: Stroke, progress: Float) {
    val leftFootCenter = Offset(w * 0.24f, h * 0.84f)
    val rightFootCenter = Offset(w * 0.76f, h * 0.84f)
    // Уменьшаем радиус лапок в зависимости от прогресса анимации
    val footRadius = w * 0.11f * progress
    // Левая лапка
    drawCircle(color = FillColor, radius = footRadius, center = leftFootCenter)
    drawCircle(
        color = OutlineColor,
        radius = footRadius,
        center = leftFootCenter,
        style = strokeStyle,
        alpha = progress
    )

    // Правая лапка
    drawCircle(color = FillColor, radius = footRadius, center = rightFootCenter)
    drawCircle(
        color = OutlineColor,
        radius = footRadius,
        center = rightFootCenter,
        style = strokeStyle,
        alpha = progress
    )
}


@Preview(showBackground = true, widthDp = 400, heightDp = 1200)
@Composable
fun BunnyRoundRect1Preview() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        Bunny(stage = PetStage.Baby, modifier = Modifier
            .fillMaxWidth(0.38f)
            .weight(1f))

        Bunny(stage = PetStage.Teenager, modifier = Modifier
            .fillMaxWidth(0.38f)
            .weight(1f))

        Bunny(stage = PetStage.Adult, modifier = Modifier
            .fillMaxWidth(0.38f)
            .weight(1f))
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 400)
@Composable
fun BunnyRoundRect2Preview() {
    Bunny(stage = PetStage.Adult)
}