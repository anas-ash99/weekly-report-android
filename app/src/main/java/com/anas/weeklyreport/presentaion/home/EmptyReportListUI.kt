package com.anas.weeklyreport.presentaion.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EmptyReportListUI() {
  Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
  ) {
      
  }
}


@Preview
@Composable
fun EmptyReportListUIPreview() {
    EmptyReportListUI()
}