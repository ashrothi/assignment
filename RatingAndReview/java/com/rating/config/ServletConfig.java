package com.rating.config;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.sql.DataSource;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.ListCompareAlgorithm;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rating.business.logic.CommonService;
import com.rating.business.logic.IoTSecurityEventListener;
import com.rating.business.logic.impl.CommonServiceImpl;
import com.rating.business.logic.impl.HibernateUnproxyAccessHook;
import com.rating.config.gson.HibernateProxyTypeAdapter;
import com.rating.interceptors.SessionValidationInterceptor;
import com.rating.interceptors.TokenValidationInterceptor;
import com.rating.utils.CommonUtils;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@SuppressWarnings("restriction")
@Configuration
@EnableWebMvc
@EnableTransactionManagement

@PropertySource("file:${APP_PROPERTIES}")

@ComponentScan(basePackages = "com.rating")
@EnableRetry
@Import({ SecurityConfig.class })
// @EnableAspectJAutoProxy
public class ServletConfig extends WebMvcConfigurerAdapter {

	private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
	private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
	private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
	private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";

	

	private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
	private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	private static final String PROPERTY_NAME_HIBERNATE_CONNECTION_CHARACTERSET = "hibernate.connection.CharSet";
	private static final String PROPERTY_NAME_HIBERNATE_CHARACTER_ENCODING = "hibernate.connection.characterEncoding";
	private static final String PROPERTY_NAME_HIBERNATE_CONNECTION_USEUNICODE = "hibernate.connection.useUnicode";

	private static final String HIBERNATE_C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
	private static final String HIBERNATE_C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
	private static final String HIBERNATE_C3P0_TIMEOUT = "hibernate.c3p0.timeout";
	private static final String HIBERNATE_C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
	private static final String HIBERNATE_C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";

	private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";

	

	public static final int CONNECTION_TIMEOUT_IN_MILLISECONDS = 1000 * 60;

	public static final int SIM_NUMBERS_API_PROCESS = 50;

	@Resource
	private Environment env;

	@Bean(destroyMethod = "shutdown")
	public Executor taskScheduler() {
		return Executors.newScheduledThreadPool(1);
	}

	/**
	 * Message externalization/internationalization
	 */
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
		String[] propertiesFiles = { "Messages", "Messages_en" };
		resourceBundleMessageSource.setBasenames(propertiesFiles);
		return resourceBundleMessageSource;
	}

	/* **************************************************************** */
	/* THYMELEAF-SPECIFIC ARTIFACTS */
	/* TemplateResolver <- TemplateEngine <- ViewResolver */
	/* **************************************************************** */

	@Bean
	public ITemplateResolver templateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setPrefix("/WEB-INF/templates/");
		resolver.setSuffix(".html");
		resolver.setCacheable(false);
		return resolver;
	}

	@Bean
	public org.thymeleaf.spring4.SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setEnableSpringELCompiler(true);
		engine.setTemplateResolver(templateResolver());
		engine.addDialect(new SpringSecurityDialect());
		engine.addDialect(new LayoutDialect());
		return engine;
	}

	@Bean
	public org.thymeleaf.spring4.view.ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		viewResolver.setCharacterEncoding("UTF-8");
		viewResolver.setRedirectHttp10Compatible(false);
		return viewResolver;
	}

	@Bean
	public String getKeyStore() {
		return env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD);

	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
		dataSource.setUrl(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
		dataSource.setUsername(env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
		dataSource.setPassword(CommonUtils.decryptString(env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD)));
		// dataSource.setPassword(env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
		return dataSource;
	}

	

	private Properties hibProperties() {
		Properties properties = new Properties();
		properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
		properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
		properties.put(PROPERTY_NAME_HIBERNATE_CONNECTION_CHARACTERSET,
				env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CONNECTION_CHARACTERSET));
		properties.put(PROPERTY_NAME_HIBERNATE_CHARACTER_ENCODING,
				env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CHARACTER_ENCODING));
		properties.put(PROPERTY_NAME_HIBERNATE_CONNECTION_USEUNICODE,
				env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CONNECTION_USEUNICODE));

		
		

		properties.put(HIBERNATE_C3P0_MIN_SIZE, env.getRequiredProperty(HIBERNATE_C3P0_MIN_SIZE));
		properties.put(HIBERNATE_C3P0_MAX_SIZE, env.getRequiredProperty(HIBERNATE_C3P0_MAX_SIZE));
		properties.put(HIBERNATE_C3P0_TIMEOUT, env.getRequiredProperty(HIBERNATE_C3P0_TIMEOUT));
		properties.put(HIBERNATE_C3P0_MAX_STATEMENTS, env.getRequiredProperty(HIBERNATE_C3P0_MAX_STATEMENTS));
		properties.put(HIBERNATE_C3P0_IDLE_TEST_PERIOD, env.getRequiredProperty(HIBERNATE_C3P0_IDLE_TEST_PERIOD));

		return properties;
	}

	@Bean(name = "RatingAndReviewTransactionManager")
	public HibernateTransactionManager transactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory().getObject());
		return transactionManager;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource());
		sessionFactoryBean
				.setPackagesToScan(env.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN).split(","));
		sessionFactoryBean.setHibernateProperties(hibProperties());
		return sessionFactoryBean;
	}



	@Bean
	public IoTSecurityEventListener ioTSecurityEventListener() {
		return new IoTSecurityEventListener(sessionFactory().getObject());
	}

	/* ******************************************************************* */
	/* Defines callback methods to customize the Java-based configuration */
	/* for Spring MVC enabled via {@code @EnableWebMvc} */
	/* ******************************************************************* */

	/**
	 * Dispatcher configuration for serving static resources
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/**").addResourceLocations("/images/").setCachePeriod(3600);
		registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(3600);
		registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(3600);
		registry.addResourceHandler("/fonts/**").addResourceLocations("/fonts/").setCachePeriod(3600);
	}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver resolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("utf-8");
		resolver.setMaxUploadSize(-1);
		return resolver;
	}

	@Bean
	public SimpleMailMessage simpleMailMessage() {
		return new SimpleMailMessage();
	}

	public HttpClient httpClient(String file, String password) throws Exception {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream instream = new FileInputStream(new File(file));
		try {
			trustStore.load(instream, password.toCharArray());
		} finally {
			instream.close();
		}

		SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
				.build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
		return HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}

	
	

	@Bean
	TokenValidationInterceptor getTokenValidationInterceptor() {
		return new TokenValidationInterceptor();
	}


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(getTokenValidationInterceptor()).addPathPatterns("/gcapi/**");
		
		registry.addInterceptor(getSessionValidationInterceptor()).addPathPatterns("/api/**");
	}

	@Bean
	public Gson gson() {
		GsonBuilder gson = new GsonBuilder();
		gson.setDateFormat("yyyy-MM-dd HH:mm:ss");
		gson.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
		// gson.setExclusionStrategies(new GsonExcludeProxiedFields());
		return gson.setPrettyPrinting().create();
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*")
				.allowedMethods("OPTIONS", "HEAD", "GET", "PUT", "POST", "DELETE", "PATCH").allowedHeaders("*")
				.exposedHeaders("Content-Type", "Access-Control-Allow-Headers", "Authorization", "X-Requested-With",
						"TimeZone")
				.allowCredentials(true);

	}


	/**
	 * @return
	 */
	@Bean
	public Validator validator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		return factory.getValidator();
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(false).favorParameter(true).parameterName("mediaType").ignoreAcceptHeader(true)
				.useJaf(false).defaultContentType(MediaType.APPLICATION_JSON)
				.mediaType("json", MediaType.APPLICATION_JSON);
	}



	@Bean
	SessionValidationInterceptor getSessionValidationInterceptor() {
		return new SessionValidationInterceptor();
	}

	@Bean(name = "commonService")
	public CommonService commonService() {
		return new CommonServiceImpl();
	}

	@Autowired
	private HibernateUnproxyAccessHook accessHook;

	@Bean
	public Javers javers() {
		// return JaversBuilder.javers().build();
		return JaversBuilder.javers().withObjectAccessHook(accessHook).withNewObjectsSnapshot(true)
				.withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
	}

	

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
		return mapper;
	}
}
