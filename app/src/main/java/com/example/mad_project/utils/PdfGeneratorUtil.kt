package com.karigar.app.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.karigar.app.data.model.Order
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PdfGeneratorUtil {

    fun generateInvoiceAndGetUri(context: Context, order: Order): Uri? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        paint.color = Color.BLACK
        
        // Header
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Karigar - Service Invoice", 180f, 80f, paint)

        // Line
        paint.strokeWidth = 2f
        canvas.drawLine(50f, 100f, 545f, 100f, paint)

        // Order Details
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        var yPos = 140f
        val lineSpacing = 25f

        canvas.drawText("Order ID: #ORD-${order.id.takeLast(6).uppercase()}", 50f, yPos, paint)
        yPos += lineSpacing
        canvas.drawText("Service: ${order.categories.joinToString(", ")}", 50f, yPos, paint)
        yPos += lineSpacing
        canvas.drawText("Date: ${order.scheduledTime}", 50f, yPos, paint)
        yPos += lineSpacing
        canvas.drawText("Worker: ${order.assignedWorkerName}", 50f, yPos, paint)
        
        // Payment Summary Header
        yPos += 40f
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Payment Summary", 50f, yPos, paint)

        // Line
        yPos += 10f
        paint.strokeWidth = 1f
        canvas.drawLine(50f, yPos, 545f, yPos, paint)

        // Payment Details
        yPos += 30f
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        
        val baseFare = order.offeredFare.toIntOrNull() ?: 800
        val extraWork = if (order.status == "Completed") 200 else 0
        val discount = 50
        val total = baseFare + extraWork - discount

        canvas.drawText("Base Fare:", 50f, yPos, paint)
        canvas.drawText("Rs $baseFare", 450f, yPos, paint)
        yPos += lineSpacing

        if (extraWork > 0) {
            canvas.drawText("Materials / Extra Work:", 50f, yPos, paint)
            canvas.drawText("Rs $extraWork", 450f, yPos, paint)
            yPos += lineSpacing
        }

        paint.color = Color.parseColor("#4CAF50") // Green for discount
        canvas.drawText("Discount:", 50f, yPos, paint)
        canvas.drawText("-Rs $discount", 450f, yPos, paint)
        yPos += lineSpacing

        // Total
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        yPos += 10f
        canvas.drawText("Total Amount:", 50f, yPos, paint)
        canvas.drawText("Rs $total", 450f, yPos, paint)

        // Footer
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.GRAY
        canvas.drawText("Thank you for using Karigar App!", 200f, 780f, paint)

        pdfDocument.finishPage(page)

        // Save PDF to cache directory
        val file = File(context.cacheDir, "Karigar_Invoice_${order.id.takeLast(6).uppercase()}.pdf")
        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: IOException) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}
