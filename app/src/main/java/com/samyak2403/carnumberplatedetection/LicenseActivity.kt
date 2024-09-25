/*
 * Created by Samyak Kamble on 9/25/24, 9:45 PM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 9/25/24, 9:45 PM
 */

package com.samyak2403.carnumberplatedetection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class LicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        // Find the toolbar by its ID
        val toolbar: Toolbar = findViewById(R.id.toolbar)


        // Set the toolbar
        setSupportActionBar(toolbar)

        // Enable the back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Handle the back button click
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Find the TextView and set the license text
        val licenseTextView: TextView = findViewById(R.id.licenseTextView)
        licenseTextView.text = getMITLicenseText()  // Set the license text here



    }

    /** 📄🚫 No changes allowed in this code! Please use as-is. 🚫📄**/

    // Method to get the MIT License text
    private fun getMITLicenseText(): String {
        return """
            MIT License
            
            Copyright (c) 2024 Samyak Kamble

            Permission is hereby granted, free of charge, to any person obtaining a copy
            of this software and associated documentation files (the "Software"), to deal
            in the Software without restriction, including without limitation the rights
            to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
            copies of the Software, and to permit persons to whom the Software is
            furnished to do so, subject to the following conditions:

            The above copyright notice and this permission notice shall be included in all
            copies or substantial portions of the Software.

            THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
            IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
            FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
            AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
            LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
            OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
            SOFTWARE.
        """.trimIndent()
    }
}
