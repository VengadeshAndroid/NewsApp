package com.mallow.newsapp.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.mallow.newsapp.R
import com.mallow.newsapp.component.BaseComponent
import com.mallow.newsapp.model.ArticlesResult
import com.mallow.newsapp.state.ArticleListState
import com.mallow.newsapp.ui.theme.Dp00
import com.mallow.newsapp.ui.theme.Dp10
import com.mallow.newsapp.ui.theme.Dp120
import com.mallow.newsapp.ui.theme.Dp15
import com.mallow.newsapp.ui.theme.Dp16
import com.mallow.newsapp.ui.theme.Dp24
import com.mallow.newsapp.ui.theme.Dp35
import com.mallow.newsapp.ui.theme.Dp40
import com.mallow.newsapp.ui.theme.Dp400
import com.mallow.newsapp.ui.theme.Dp50
import com.mallow.newsapp.ui.theme.Dp8
import com.mallow.newsapp.ui.theme.Sp14
import com.mallow.newsapp.ui.theme.Sp16
import com.mallow.newsapp.ui.theme.Sp20
import com.mallow.newsapp.util.Constants.SharedKey.COUNT
import com.mallow.newsapp.util.ThemePreview
import com.mallow.newsapp.util.formatDate
import com.mallow.newsapp.viewmodel.ArticlesListViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class, FlowPreview::class)
@Composable
fun DetailScreen(navController: NavHostController, articleid: Int?) {

    val viewModel: ArticlesListViewModel = hiltViewModel()

    var articleId by remember { mutableStateOf(articleid) }
    var noDataFound by remember { mutableStateOf("") }
    var articlesResult by remember { mutableStateOf<ArticlesResult?>(null) }

    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState()

    val initialArticleId by remember { mutableStateOf(articleid ?: 0) }
    var previousPageIndex by remember { mutableStateOf(pagerState.currentPage) }

    LaunchedEffect(pagerState) {
        // Use snapshotFlow to observe pageIndex changes distinctly
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .debounce(300L)
            .collect { pageIndex ->
                // Calculate the new articleId based on the swipe direction
                articleId = initialArticleId + (pageIndex - previousPageIndex)
                // Update previousPageIndex to reflect the current swipe position
                previousPageIndex = pageIndex
                // Ensure valid articleId before making the API call
                articleId?.let {
                    if (it >= 0) { // Avoid invalid IDs
                        viewModel.articleDetails(it)
                    }
                }
            }
    }

    BaseComponent(viewModel = viewModel, stateObserver = { state ->
        when (state) {
            is ArticleListState.ArticleDetailSuccessState -> {
                articlesResult = state.data
                noDataFound = ""
                viewModel.dismissProgressBar()
            }

            is ArticleListState.ShowMessage -> {
                noDataFound = state.msg
                viewModel.showToast(state.msg)
                viewModel.dismissProgressBar()
            }

            else -> {
                viewModel.dismissProgressBar()
            }
        }
    }) {

        articlesResult?.let { articles ->
            // Use HorizontalPager to implement swipe navigation
            HorizontalPager(
                count = COUNT.toInt(), // Set the number of items
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { _ ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .background(Color(0xFFFFFFFF))
                ) {

                    when {
                        noDataFound.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dp400)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = Dp16,
                                            topEnd = Dp16,
                                            bottomStart = Dp00,
                                            bottomEnd = Dp00
                                        )
                                    )
                            ) {
                                val imageUrl = articles.image_url

                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .placeholder(R.drawable.no_image)
                                        .error(R.drawable.no_image)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Cycling",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                )

                                Button(
                                    onClick = { /* Handle click action */ },
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(start = Dp10, end = Dp10, bottom = Dp120)
                                        .width(Dp120)
                                        .height(Dp35)
                                        .clip(RoundedCornerShape(Dp10)),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFF007BFF
                                        )
                                    )
                                ) {
                                    Text(
                                        text = "Sports",
                                        fontSize = Sp16,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White
                                    )
                                }

                                articles.title?.let {
                                    Text(
                                        text = it,
                                        fontSize = Sp20,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(Dp16, bottom = Dp50),
                                        maxLines = 2
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomStart)
                                        .padding(start = Dp16, bottom = Dp15)
                                ) {
                                    articles.news_site?.let { subtitle ->
                                        Text(
                                            text = subtitle,
                                            fontSize = Sp14,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(Dp8))
                                    // Date text
                                    articles.published_at?.let { date ->
                                        Text(
                                            text = formatDate(date),
                                            fontSize = Sp14,
                                            color = Color.White
                                        )
                                    }
                                }

                                // TopAppBar with transparent background
                                TopAppBar(
                                    title = {},
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = Color.Transparent,
                                        titleContentColor = Color.White,
                                        actionIconContentColor = Color.White
                                    ),
                                    navigationIcon = {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        Color.LightGray.copy(alpha = 0.5f),
                                                        shape = CircleShape
                                                    )
                                                    .padding(8.dp)
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_back_arrow),
                                                    contentDescription = "Back Icon",
                                                    modifier = Modifier.size(Dp24),
                                                    colorFilter = ColorFilter.tint(Color.White)
                                                )
                                            }
                                        }
                                    },
                                    actions = {
                                        IconButton(onClick = { viewModel.showToast("News Bookmarked") }) {
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        Color.LightGray.copy(alpha = 0.5f),
                                                        shape = CircleShape
                                                    )
                                                    .padding(8.dp)
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_bookmark),
                                                    contentDescription = "Bookmark Icon",
                                                    modifier = Modifier.size(Dp24),
                                                    colorFilter = ColorFilter.tint(Color.White)
                                                )
                                            }
                                        }

                                        IconButton(onClick = { viewModel.showToast("More Details") }) {
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        Color.LightGray.copy(alpha = 0.5f),
                                                        shape = CircleShape
                                                    )
                                                    .padding(8.dp)
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_more),
                                                    contentDescription = "More Icon",
                                                    modifier = Modifier.size(Dp24),
                                                    colorFilter = ColorFilter.tint(Color.White)
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.TopStart)
                                )
                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(topStart = Dp16, topEnd = Dp16)),
                                elevation = CardDefaults.cardElevation(Dp8),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(all = Dp10)
                                        .fillMaxWidth()
                                        .background(Color(0xFFFFFFFF))
                                ) {
                                    articles.news_site?.let {
                                        Row(
                                            modifier = Modifier.padding(start = Dp10, top = Dp10),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val imageUrl = articles.image_url
                                            Image(
                                                painter = rememberImagePainter(imageUrl),
                                                contentDescription = "Cycling",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(Dp40)
                                                    .clip(CircleShape)
                                                    .background(Color.Gray)
                                            )
                                            Text(
                                                text = it,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(start = Dp8)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(Dp8))

                                    articles.summary?.let {
                                        Text(
                                            text = it,
                                            fontSize = Sp16,
                                            color = Color.DarkGray,
                                            textAlign = TextAlign.Justify,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                        )
                                    }
                                }
                            }


                        }

                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFF5F5F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                NoDataFound(
                                    message = "No data available"
                                )
                            }
                        }
                    }
                }

            }
        }

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
@ThemePreview
fun DetailPreview() {
    DetailScreen(
        navController = rememberNavController(),
        2
    )
}