package com.example.gradereportapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // ── Data Model ──
    data class Subject(val name: String, val obtained: Int, val total: Int)

    private val subjects = mutableListOf<Subject>()
    private lateinit var tableLayout: TableLayout
    private lateinit var tvGPA: TextView
    private var summaryRow: TableRow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ── Find Views ──
        tableLayout = findViewById(R.id.tableLayout)
        tvGPA       = findViewById(R.id.tvGPA)

        val etSubjectName = findViewById<EditText>(R.id.etSubjectName)
        val etObtained    = findViewById<EditText>(R.id.etObtained)
        val etTotal       = findViewById<EditText>(R.id.etTotal)
        val btnAdd        = findViewById<Button>(R.id.btnAdd)
        val btnPrint      = findViewById<Button>(R.id.btnPrint)

        // ── 6. Pre-load 6 Subjects ──
        val initialSubjects = listOf(
            Subject("Mathematics",   85,  100),
            Subject("Physics",       72,  100),
            Subject("Comp. Science", 95,  100),
            Subject("English",       65,  100),
            Subject("Chemistry",     38,  100),
            Subject("Statistics",    78,  100)
        )

        for (subject in initialSubjects) {
            subjects.add(subject)
            addSubjectRow(subject)
        }
        updateSummary()

        // ── Add Button Click ──
        btnAdd.setOnClickListener {
            val name     = etSubjectName.text.toString().trim()
            val obtained = etObtained.text.toString().toIntOrNull()
            val total    = etTotal.text.toString().toIntOrNull()

            // Validate inputs
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter subject name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (obtained == null || total == null ||
                total <= 0 || obtained < 0 || obtained > total
            ) {
                Toast.makeText(this, "Please enter valid marks", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val subject = Subject(name, obtained, total)
            subjects.add(subject)
            addSubjectRow(subject)
            updateSummary()

            // Clear fields
            etSubjectName.text.clear()
            etObtained.text.clear()
            etTotal.text.clear()

            Toast.makeText(this, "$name added!", Toast.LENGTH_SHORT).show()
        }

        // ── Print / Share Button Click ──
        btnPrint.setOnClickListener {
            Toast.makeText(this, "Report shared successfully!", Toast.LENGTH_LONG).show()
        }
    }

    // ======================================================================
    // GRADE CALCULATION LOGIC
    // ======================================================================

    private fun getGrade(percentage: Double): String = when {
        percentage >= 90 -> "A+"
        percentage >= 80 -> "A"
        percentage >= 70 -> "B+"
        percentage >= 60 -> "B"
        percentage >= 50 -> "C"
        percentage >= 40 -> "D"
        else             -> "F"
    }

    private fun getGradePoints(grade: String): Double = when (grade) {
        "A+" -> 4.0
        "A"  -> 3.7
        "B+" -> 3.3
        "B"  -> 3.0
        "C"  -> 2.0
        "D"  -> 1.0
        else -> 0.0          // F
    }

    // ======================================================================
    // ADD A SUBJECT ROW DYNAMICALLY
    // ======================================================================

    private fun addSubjectRow(subject: Subject) {

        // Remove summary row so new data row goes before it
        if (summaryRow != null) {
            tableLayout.removeView(summaryRow)
        }

        val percentage = (subject.obtained.toDouble() / subject.total) * 100
        val grade  = getGrade(percentage)
        val isFail = grade == "F"
        val index  = subjects.size - 1          // 0-based position

        // ── Create TableRow ──
        val row = TableRow(this).apply {
            setPadding(2, 2, 2, 2)

            // Fail → red  |  Pass → alternating greens
            when {
                isFail          -> setBackgroundColor(Color.parseColor("#FFCDD2"))
                index % 2 == 0  -> setBackgroundColor(Color.parseColor("#C8E6C9"))
                else            -> setBackgroundColor(Color.parseColor("#A5D6A7"))
            }
        }

        row.addView(createCell(subject.name, Gravity.START))
        row.addView(createCell(subject.obtained.toString(), Gravity.CENTER))
        row.addView(createCell(subject.total.toString(), Gravity.CENTER))
        row.addView(createCell(grade, Gravity.CENTER, isBold = true))

        tableLayout.addView(row)
    }

    // ── Helper: build one cell TextView ──
    private fun createCell(
        text: String,
        gravity: Int,
        isBold: Boolean = false
    ): TextView = TextView(this).apply {
        this.text    = text
        this.gravity = gravity
        setPadding(10, 14, 10, 14)
        textSize = 14f
        if (isBold) setTypeface(null, Typeface.BOLD)
    }

    // ======================================================================
    // 7. SUMMARY ROW  +  8. GPA  (recalculated every time)
    // ======================================================================

    private fun updateSummary() {

        // Remove old summary row if present
        if (summaryRow != null) {
            tableLayout.removeView(summaryRow)
        }

        // ── Count pass / fail ──
        val passed = subjects.count {
            getGrade((it.obtained.toDouble() / it.total) * 100) != "F"
        }
        val failed = subjects.size - passed

        // ── Build summary row with layout_span = 4 ──
        summaryRow = TableRow(this).apply {
            setBackgroundColor(Color.parseColor("#37474F"))
            setPadding(2, 2, 2, 2)
        }

        val summaryText = TextView(this).apply {
            text = "Total: ${subjects.size}   |   Passed: $passed   |   Failed: $failed"
            setTextColor(Color.WHITE)
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(10, 14, 10, 14)
            // *** layout_span spans all 4 columns ***
            layoutParams = TableRow.LayoutParams().apply {
                span = 4
            }
        }

        summaryRow!!.addView(summaryText)
        tableLayout.addView(summaryRow)

        // ── Calculate GPA ──
        var totalPoints = 0.0
        for (s in subjects) {
            val pct = (s.obtained.toDouble() / s.total) * 100
            totalPoints += getGradePoints(getGrade(pct))
        }
        val gpa = if (subjects.isNotEmpty()) totalPoints / subjects.size else 0.0
        tvGPA.text = String.format("GPA: %.2f", gpa)
    }
}