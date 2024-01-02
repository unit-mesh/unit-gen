@Test
void shouldCreateBlog() throws Exception {
// given
String title = "Test Title";
String content = "Test Content";
String author = "Test Author";

    CreateBlogRequest request = new CreateBlogRequest();
    request.setTitle(title);
    request.setContent(content);
    request.setUser(author);

    BlogPost blogPost = new BlogPost();
    blogPost.setTitle(title);
    blogPost.setContent(content);
    blogPost.setAuthor(author);

    BlogPost createdBlogPost = new BlogPost();
    // set expected values for createdBlogPost

    given(blogService.createBlog(any(BlogPost.class))).willReturn(createdBlogPost);

    // when
    mockMvc.perform(post("/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is(title)))
            .andExpect(jsonPath("$.content", is(content)))
            .andExpect(jsonPath("$.author", is(author)));


    // then
    verify(blogService).createBlog(any(BlogPost.class));
}