package com.mallow.newsapp.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mallow.newsapp.R
import com.mallow.newsapp.component.BaseComponent
import com.mallow.newsapp.model.ArticlesResult
import com.mallow.newsapp.state.ArticleListState
import com.mallow.newsapp.ui.theme.Dp05
import com.mallow.newsapp.ui.theme.Dp10
import com.mallow.newsapp.ui.theme.Dp16
import com.mallow.newsapp.ui.theme.Dp20
import com.mallow.newsapp.ui.theme.Dp24
import com.mallow.newsapp.ui.theme.Dp30
import com.mallow.newsapp.ui.theme.Dp8
import com.mallow.newsapp.ui.theme.Sp14
import com.mallow.newsapp.ui.theme.Sp16
import com.mallow.newsapp.ui.theme.Sp20
import com.mallow.newsapp.ui.theme.Sp30
import com.mallow.newsapp.util.Constants.SharedKey.COUNT
import com.mallow.newsapp.util.Constants.SharedKey.NEXT
import com.mallow.newsapp.util.ThemePreview
import com.mallow.newsapp.util.formatDate
import com.mallow.newsapp.util.hasNetworkConnection
import com.mallow.newsapp.viewmodel.ArticlesListViewModel
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlesListScreen(navController: NavHostController) {

    val viewModel: ArticlesListViewModel = hiltViewModel()

    val preferenceList = remember { mutableStateListOf<String>() }
    var articlesResult by remember { mutableStateOf<List<ArticlesResult>>(emptyList()) }

    var isRefreshing by remember { mutableStateOf(false) }

    // Pull-to-refresh state
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    val doubleBackToExitPressedOnce = remember { mutableStateOf(false) }


    LaunchedEffect(key1 = Unit) {
        viewModel.showProgressBar()
        when {
            viewModel.sharedPrefManager.preference.isNotEmpty() -> {
                viewModel.articleList(search = viewModel.sharedPrefManager.preference)
            }

            else -> {
                viewModel.articleList()
            }
        }
        viewModel.preferenceList()
    }

    // Reset the flag after 2 seconds if the second back press doesn't happen
    LaunchedEffect(doubleBackToExitPressedOnce.value) {
        when {
            doubleBackToExitPressedOnce.value -> {
                delay(2000)  // Wait for 2 seconds
                doubleBackToExitPressedOnce.value = false
            }
        }
    }

    BaseComponent(viewModel = viewModel, stateObserver = { state ->
        when (state) {
            is ArticleListState.ArticleListSuccessState -> {
                isRefreshing = false
                when (viewModel.currentOffset) {
                    0 -> {
                        articlesResult = state.data.results!!
                    }

                    else -> {
                        articlesResult = articlesResult + state.data.results!!
                    }
                }
                COUNT = state.data.count.toString()
                NEXT = state.data.next.toString()
                viewModel.dismissProgressBar()
            }

            is ArticleListState.PreferenceListSuccessState -> {
                preferenceList.clear()
                state.data.news_sites?.let { preferenceList.addAll(it.filterNotNull()) }
                viewModel.dismissProgressBar()
            }

            is ArticleListState.ShowMessage -> {
                viewModel.showToast(state.msg)
                isRefreshing = false
            }

            else -> {
                viewModel.dismissProgressBar()
                isRefreshing = false
            }
        }
    }) {

        SwipeRefresh(state = swipeRefreshState, onRefresh = {
            isRefreshing = true
            viewModel.articleList()

        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFFFFFFF))
            ) {
                // Top Left Back Icon
                TopAppBar(
                    title = {}, colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        actionIconContentColor = Color.Black
                    ), navigationIcon = {
                        IconButton(onClick = {
                            if (doubleBackToExitPressedOnce.value) {
                                // Close the app or navigate back
                                // You can use finish() or System.exit(0) to close the app
                                System.exit(0)
                            } else {
                                doubleBackToExitPressedOnce.value = true
                                viewModel.showToast("Press back again to exit")
                            }
                        }) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color.LightGray.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .padding(Dp8)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_back_arrow),
                                    contentDescription = "Back Icon",
                                    modifier = Modifier.size(Dp24),
                                    colorFilter = ColorFilter.tint(Color.Black)
                                )
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(id = R.string.discover_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold, fontSize = Sp30, color = Color.Black
                    ),
                    modifier = Modifier.padding(start = Dp16, end = Dp16)
                )

                Text(
                    text = stringResource(id = R.string.news_from),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = Sp14,
                        color = Color.LightGray
                    ),
                    modifier = Modifier.padding(start = Dp16, end = Dp16),
                    maxLines = 1
                )

                SearchBar(viewModel, navController)

                HorizontalList(viewModel, preferenceList)

                if (articlesResult.isNotEmpty()) {
                    ListViewItem(articlesResult = articlesResult,
                        navController = navController,
                        isLoading = viewModel.isLoading.value,
                        onLoadMore = {
                            viewModel.loadNextPage()
                        })
                } else {

                    isRefreshing = false

                    NoDataFound()
                }

            }
        }
    }
}

@Composable
fun SearchBar(viewModel: ArticlesListViewModel, navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current

    OutlinedTextField(
        value = searchText,
        onValueChange = { newText ->
            searchText = newText
            if (context.hasNetworkConnection()) {
                if (newText.length >= 3) {
                    viewModel.showProgressBar()
                    viewModel.articleList(search = newText)
                } else if (newText.isEmpty()) {
                    viewModel.showProgressBar()
                    viewModel.articleList()
                }
            } else {
                viewModel.showToast("No network found. Please check your connection")
            }

        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(50.dp) // Reduced height
            .clip(RoundedCornerShape(50))
            .background(Color.LightGray.copy(alpha = 0.2f)),
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Back Icon",
                modifier = Modifier.size(Dp24),
                colorFilter = ColorFilter.tint(Color.Black) // Optional: Tint the icon color
            )
        },
        trailingIcon = {
            IconButton(onClick = {
                navController.navigate("preference")
            }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "Filter Icon",
                    modifier = Modifier.size(Dp24),
                    colorFilter = ColorFilter.tint(Color.Black) // Optional: Tint the icon color
                )
            }
        },
        placeholder = {
            Text(
                text = "Search", style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Normal, fontSize = Sp14, color = Color.LightGray
                )
            )
        },
        shape = RoundedCornerShape(50),
    )
}

@Composable
fun HorizontalList(viewModel: ArticlesListViewModel, stateData: List<String?>) {

    val selectedIndex = remember { mutableStateOf(-1) }
    val selected = remember { mutableStateOf(false) }

    LaunchedEffect(selectedIndex.value) {
        if (selected.value) {
            val searchQuery = when {
                selectedIndex.value >= 0 -> {
                    stateData[selectedIndex.value]
                }

                else -> {
                    null
                }
            }
            viewModel.articleList(search = searchQuery)
            selected.value = false
        }

    }


    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        itemsIndexed(stateData) { index, file ->
            Box(modifier = Modifier
                .padding(all = Dp05)
                .width(150.dp) // Adjust the width as needed
                .background(
                    when (selectedIndex.value) {
                        index -> Color(0xFF0172CB)
                        else -> Color.LightGray.copy(alpha = 0.3f)
                    }, // Default color
                    shape = RoundedCornerShape(Dp30)
                )
                .clickable {
                    selected.value = true
                    // Update selectedIndex when the user clicks an item
                    when (selectedIndex.value) {
                        index -> {
                            selectedIndex.value = -1 // Deselect
                        }

                        else -> {
                            selectedIndex.value = index // Select new item
                        }
                    }
                }) {
                if (file != null) {
                    Text(
                        text = file,
                        modifier = Modifier
                            .padding(all = Dp8)
                            .align(Alignment.Center),
                        color = when (selectedIndex.value) {
                            index -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListViewItem(articlesResult: List<ArticlesResult>, navController: NavHostController, isLoading: Boolean,
                 onLoadMore: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dp16)
    ) {
        itemsIndexed(articlesResult) { index, article ->
            ArticleRow(article, navController)

            // Trigger load more when reaching the end of the list
            if (index == articlesResult.lastIndex && !isLoading) {
                LaunchedEffect(Unit) { onLoadMore() }
            }
        }

        // Show the loader only if data is being fetched
        when {
            isLoading -> {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dp16)
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ArticleRow(article: ArticlesResult, navController: NavHostController) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            navController.navigate("detail/${article.id}")
        }
        .padding(vertical = Dp8)) {
        AsyncImage(
            model = article.image_url,
            contentDescription = null,
            placeholder = painterResource(R.drawable.no_image),
            error = painterResource(R.drawable.no_image),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(Dp10))
        )
        Spacer(modifier = Modifier.width(Dp8))
        Column {
            Text(
                text = article.news_site ?: "No Title",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Normal, fontSize = Sp14, color = Color.LightGray
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = article.summary?.replace("\n", "") ?: "No Description",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold, fontSize = Sp16, color = Color.Black
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(Dp8))

            Row(verticalAlignment = Alignment.CenterVertically) {

                val imageUrl = article.image_url
                AsyncImage(
                    model = imageUrl, // The URL of the image to load
                    contentDescription = "Cycling",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(20.dp) // Corrected to 20.dp instead of Dp20
                        .clip(CircleShape)
                        .background(Color.Gray),
                    placeholder = painterResource(id = R.drawable.no_image),
                    error = painterResource(id = R.drawable.no_image)
                )

                Spacer(modifier = Modifier.width(Dp8))

                Text(
                    text = article.news_site ?: "Unknown",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Normal, fontSize = Sp14, color = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.width(Dp8))

                Text(text = article.published_at?.let { formatDate(it) } ?: "Unknown Date",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Normal, fontSize = Sp14, color = Color.LightGray
                    ))
            }
        }
    }
}


@Composable
fun NoDataFound(message: String = "No Data Available") {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(Dp16),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            fontSize = Sp20,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dp8)) // Space between texts

        Text(
            text = "Please try again later.",
            fontSize = Sp16,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@ThemePreview
fun ArticlesListPreview() {
    ArticlesListScreen(navController = rememberNavController())
}