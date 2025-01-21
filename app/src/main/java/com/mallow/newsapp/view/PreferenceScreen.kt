package com.mallow.newsapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mallow.newsapp.component.BaseComponent
import com.mallow.newsapp.state.ArticleListState
import com.mallow.newsapp.ui.theme.Dp10
import com.mallow.newsapp.ui.theme.Dp5
import com.mallow.newsapp.ui.theme.Dp50
import com.mallow.newsapp.ui.theme.Dp80
import com.mallow.newsapp.ui.theme.skyBlue
import com.mallow.newsapp.util.ThemePreview
import com.mallow.newsapp.viewmodel.ArticlesListViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreen(navController: NavHostController) {

    val viewModel: ArticlesListViewModel = hiltViewModel()

    var searchQuery by remember { mutableStateOf("") }
    var selectNews by remember { mutableStateOf("") }

    val preferenceList = remember { mutableStateListOf<String>() }
    val filteredList = remember { mutableStateListOf<String>() }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(key1 = Unit) {
        delay(300L)
        viewModel.showProgressBar()
        viewModel.preferenceList()
    }

    BaseComponent(viewModel = viewModel, stateObserver = { state ->
        when (state) {
            is ArticleListState.PreferenceListSuccessState -> {
                preferenceList.clear()
                state.data.news_sites?.let { preferenceList.addAll(it.filterNotNull()) }
                filteredList.clear()
                filteredList.addAll(preferenceList)
                viewModel.dismissProgressBar()
            }

            is ArticleListState.ShowMessage -> {
                viewModel.showToast(state.msg)
            }

            else -> {
                viewModel.dismissProgressBar()
            }
        }
    }) {
        // Apply filtering logic whenever the search query changes
        LaunchedEffect(searchQuery) {
            filteredList.clear()
            if (searchQuery.isNotEmpty()) {
                filteredList.addAll(preferenceList.filter {
                    it.contains(
                        searchQuery,
                        ignoreCase = true
                    )
                })
            } else {
                filteredList.addAll(preferenceList)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Column that takes up the remaining space above the button
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = Dp80)
            ) {
                // TopBar with Back Button and Title
                TopAppBar(
                    title = {
                        Text(
                            text = "Choose your News Sources",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )

                // Search Bar
                SearchBar(searchQuery = searchQuery, onSearchQueryChange = { searchQuery = it })

                // List of News Sources
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(filteredList) { index, name ->
                        NewsSourceListItem(
                            name = name,
                            isEven = index % 2 == 0,
                            isSelected = selectedIndex == index, // Check if item is selected
                            onItemClick = {
                                selectedIndex = if (selectedIndex == index) null else index
                                if (selectedIndex == null) {
                                    selectNews = ""
                                } else {
                                    selectNews = name
                                }
                            }
                        )
                    }
                }
            }

            // Next Button at the bottom
            Button(
                onClick = {
                    viewModel.sharedPrefManager.preference = selectNews
                    navController.navigate("list")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dp10)
                    .background(Color(0xFF007BFF), shape = RoundedCornerShape(5.dp))
                    .align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
            ) {
                Text("Next", color = Color.White)
            }
        }
    }

}


@Composable
fun SearchBar(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        label = { Text("Search") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        singleLine = true
    )
}

@Composable
fun NewsSourceListItem(
    name: String,
    isEven: Boolean,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    // Default and selected colors
    val firstColor = if (isEven) skyBlue else Color(0xFFE8EDF1)
    val backgroundColor =
        if (isSelected) Color(0xFF4CAF50) else firstColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dp10)
            .background(backgroundColor, shape = RoundedCornerShape(Dp5))
            .padding(Dp10)
            .clickable { onItemClick() }
    ) {
        Text(
            text = name,
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium
        )

    }
}


@Composable
@ThemePreview
fun PreferencePreview() {
    PreferenceScreen(navController = rememberNavController())
}