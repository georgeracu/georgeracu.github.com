---
layout: post
title: "Spring Boot testing - Focus on what matters: the code changes that we performed"
date: 2022-01-31 07:00:00 +0000
tags: [programming, spring boot, java, testing]
mathjax: false
---

## Testing in Spring Boot

Time and time again we find ouserlves in the situation of writing a new micro-service in SpringBoot. Part of the TDD process is to start writing tests first. As not all the times is possible to write the tests first, we will go back and add the tests after we wrote the code. As there are many reasons to not do that, I will not enumerate them here. Let's focus on how to test what matters, versus testing what was already tested.

Imports omitted for code brevity.

### Initial Java code

```java
package com.georgeracu.blog.springboot.application.config;

public class CustomWebMvcConfigurer implements WebMvcConfigurer {
    
    @Value("${config.origins.allowed}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if(StringUtils.isNotEmpty(allowedOrigins)) {
            registry
                .addMapping("/**")
                .allowedOrigins(allowedOrigins.split(","));

        }
    }
}
```

Once our production code has been written, let's see how a test could look like.

```java
package com.georgeracu.blog.springboot.application.config;

class CustomWebMvcConfigurerTest {
    private CustomWebMvcConfigurer configurer;
    private TestCorsRegistry registry;

    @BeforeEach
    void setup() {
        configurer = new CustomWebMvcConfigurer();
        registry = new TestCorsRegistry();
    }

    @ParameterizedTest
    @MethodSource("argsForCorsProvider")
    void shouldAddCorsMappingsWhenEnabled(String allowedOrigins, int expectedSize) {
        // arrange
        ReflectionTestUtils.setField(configurer, "allowedOrigins", allowedOrigins);

        // act
        configurer.addCorsMappings(registry);

        // assert
        assertEquals(1, registry.getCorsConfigurations().size());
    }

    private static Stream<Arguments> argsForCorsProvider() {
        return Stream.of(Arguments.of("http://example.com", 1));
    }

    class TestCorsRegistry extends CorsRegistry {
        public Map<String, CorsConfiguration> getCorsConfiguration() {
            return super.getCorsConfiguration();
        }
    }
}
```

So far, we wrote a test that is testing that our CORS origins are added to the list of allowed origins. All nice and simple, but a few observations:

* The test is testing that the CorsRegistry adds a mapping to the list of the allowed origins. This feels that it was tested already by the creators of the framework;
* We are not focusing that we actually tested our custom code: adding several values to the list of the allowed origins;
* The class TestCorsRegistry is not needed when there are powerfull mocking frameworks that allow us to mock that object and assert that custom behaviour has happened on the mock itself;
* Field injection is in our way of actually writing simpler unit tests;

Lets's see how this code would look like with a bit of refactoring.

```java
package com.georgeracu.blog.springboot.application.config;

@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {
    
    private String allowedOrigins;

    public CustomWebMvcConfigurer(@Value("${config.origins.allowed}") String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if(StringUtils.isNotEmpty(allowedOrigins)) {
            registry
                .addMapping("/**")
                .allowedOrigins(allowedOrigins.split(","));

        }
    }
}
```

```java
package com.georgeracu.blog.springboot.application.config;

class CustomWebMvcConfigurerTest {
    private CustomWebMvcConfigurer configurer;
    private CorsRegistry registry;
    private CorsRegistration corsRegistration;

    @BeforeEach
    void setup() {
        registry = Mockito.mock(CorsRegistry.class);
        corsRegistration = Mockito.mock(CorsRegistration.class);
    }

    @ParameterizedTest
    @MethodSource("argsForCorsProvider")
    void shouldAddCorsMappingsWhenEnabled(String allowedOrigins) {
        // arrange
        when(registry.addMapping("/**")).thenReturn(corsRegistration);
        configurer = new CustomWebMvcConfigurer(allowedOrigins);

        // act
        configurer.addCorsMappings(registry);

        // assert
        verify(registry).addMapping("/**");
        verify(corsRegistration).allowedOrigins(allowedOrigins);
    }

    private static Stream<Arguments> argsForCorsProvider() {
        return Stream.of(Arguments.of("http://example.com"));
    }
}
```