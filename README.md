Assignment Rating MAnagement Module


The attached json file contains the reviews for Amazon Alexa app from Google and Apple stores. 

Develop a REST web service (in Node.js) that does the following 

Accepts reviews and stores them 
Allows to fetches reviews 
Reviews can be filtered by date, store type or rating. 
All filters are optional; fetch all reviews if no filters are applied. 
Allows to get average monthly ratings per store. 
Allows to get total ratings for each category. Meaning, how many 5*, 4*, 3* and so on 
A 3rd party browser application shall be able to access the service. 
Implement test cases to validate the code. 

Code Structure For the Assignment 


[D] 2.0
|---- .classpath
|---- .project
|-[D] .settings
|  |---- .jsdtscope
|  |---- org.eclipse.core.resources.prefs
|  |---- org.eclipse.jdt.core.prefs
|  |---- org.eclipse.m2e.core.prefs
|  |---- org.eclipse.wst.common.component
|  |---- org.eclipse.wst.common.project.facet.core.xml
|  |---- org.eclipse.wst.jsdt.ui.superType.container
|  |---- org.eclipse.wst.jsdt.ui.superType.name
|  `---- org.eclipse.wst.validation.prefs
|-[D] build
|  `---- build.xml
|---- pom.xml
|-[D] src
|  |-[D] java
|  |  |-[D] com
|  |  |  `-[D] rating
|  |  |  |  |-[D] bo
|  |  |  |  |  |---- ApiUser.java
|  |  |  |  |  |-[D] common
|  |  |  |  |  |  `---- BaseEntity.java
|  |  |  |  |  |---- RatingAndReview.java
|  |  |  |  |  |---- TokenManager.java
|  |  |  |  |  |---- User.java
|  |  |  |  |  `---- UserDetails.java
|  |  |  |  |-[D] business
|  |  |  |  |  `-[D] logic
|  |  |  |  |  |  |---- ApiUserService.java
|  |  |  |  |  |  |---- AuthService.java
|  |  |  |  |  |  |---- CommonService.java
|  |  |  |  |  |  |---- CrudService.java
|  |  |  |  |  |  |---- CustomUserDetailsService.java
|  |  |  |  |  |  |---- EncryptionDecryptionService.java
|  |  |  |  |  |  |-[D] impl
|  |  |  |  |  |  |  |---- ApiUserServiceImpl.java
|  |  |  |  |  |  |  |---- AuthServiceImpl.java
|  |  |  |  |  |  |  |---- CommonServiceImpl.java
|  |  |  |  |  |  |  |---- CrudServiceImpl.java
|  |  |  |  |  |  |  |---- CustomUserDetailsServiceImpl.java
|  |  |  |  |  |  |  |---- EncryptionDecryptionServiceImpl.java
|  |  |  |  |  |  |  |---- HibernateUnproxyAccessHook.java
|  |  |  |  |  |  |  |---- RatingAndReviewServiceImpl.java
|  |  |  |  |  |  |  |---- SessionValidationServiceImpl.java
|  |  |  |  |  |  |  |---- TokenManagerServiceImpl.java
|  |  |  |  |  |  |  |---- TokenValidationServiceImpl.java
|  |  |  |  |  |  |  `---- UserServiceImpl.java
|  |  |  |  |  |  |---- IoTSecurityEventListener.java
|  |  |  |  |  |  |---- RatingAndReviewService.java
|  |  |  |  |  |  |---- ServiceCommonUtils.java
|  |  |  |  |  |  |---- SessionValidationService.java
|  |  |  |  |  |  |---- TokenManagerService.java
|  |  |  |  |  |  |---- TokenValidationService.java
|  |  |  |  |  |  `---- UserService.java
|  |  |  |  |-[D] config
|  |  |  |  |  |-[D] gson
|  |  |  |  |  |  `---- HibernateProxyTypeAdapter.java
|  |  |  |  |  |---- SecurityConfig.java
|  |  |  |  |  |---- ServletConfig.java
|  |  |  |  |  |---- SpringMvcInitializer.java
|  |  |  |  |  |---- SpringSecurityInitializer.java
|  |  |  |  |  `---- WebSocketConfig.java
|  |  |  |  |-[D] dl
|  |  |  |  |  |---- CrudRepository.java
|  |  |  |  |  |---- QueryFilterTemplate.java
|  |  |  |  |  `---- Repository.java
|  |  |  |  |-[D] dto
|  |  |  |  |  |---- ApiResponse.java
|  |  |  |  |  |---- ApiUserDTO.java
|  |  |  |  |  |---- AuthRequest.java
|  |  |  |  |  |---- AuthResponse.java
|  |  |  |  |  |---- BaseResponse.java
|  |  |  |  |  |-[D] common
|  |  |  |  |  |  `---- AbstractItem.java
|  |  |  |  |  |---- DataTableRequestDto.java
|  |  |  |  |  |---- LoggedInUserDto.java
|  |  |  |  |  |---- ReactApiUserDto.java
|  |  |  |  |  |---- ReactUserDto.java
|  |  |  |  |  `---- UserDTO.java
|  |  |  |  |-[D] enums
|  |  |  |  |  |---- Error.java
|  |  |  |  |  `---- IdDisplayNameProvidable.java
|  |  |  |  |-[D] exc
|  |  |  |  |  |---- ApiErrorDto.java
|  |  |  |  |  |---- ApiException.java
|  |  |  |  |  |---- ApiFieldErrorDto.java
|  |  |  |  |  |---- FieldErrorDto.java
|  |  |  |  |  `---- RatingAndReviewException.java
|  |  |  |  |-[D] interceptors
|  |  |  |  |  |---- APIAccessValidationInterceptor.java
|  |  |  |  |  |---- RequestWrapper.java
|  |  |  |  |  |---- SessionValidationInterceptor.java
|  |  |  |  |  |---- TokenValidationInterceptor.java
|  |  |  |  |  `---- XSSRequestWrapper.java
|  |  |  |  |-[D] react
|  |  |  |  |  `-[D] web
|  |  |  |  |  |  `-[D] controller
|  |  |  |  |  |  |  |---- AdminViewController.java
|  |  |  |  |  |  |  |---- ApiController.java
|  |  |  |  |  |  |  |---- PasswrdController.java
|  |  |  |  |  |  |  |---- RatingAndReviewController.java
|  |  |  |  |  |  |  `---- SignInController.java
|  |  |  |  |-[D] response
|  |  |  |  |  `---- Message.java
|  |  |  |  |-[D] serializers
|  |  |  |  |  `---- IdDisplayNameSerializer.java
|  |  |  |  |-[D] transform
|  |  |  |  |  |---- RatingAndReviewFieldMapper.java
|  |  |  |  |  |---- ResponseTransformer.java
|  |  |  |  |  |---- TransformProcessor.java
|  |  |  |  |  `---- TransformProcessorImpl.java
|  |  |  |  `-[D] utils
|  |  |  |  |  |---- CommonUtils.java
|  |  |  |  |  |-[D] datatable
|  |  |  |  |  |  |---- ColumnParameter.java
|  |  |  |  |  |  |---- DataTableResponse.java
|  |  |  |  |  |  |---- DataTablesInput.java
|  |  |  |  |  |  |---- OrderParameter.java
|  |  |  |  |  |  `---- SearchParameter.java
|  |  |  |  |  |---- Encryption.java
|  |  |  |  |  |---- JwtApiTokenUtil.java
|  |  |  |  |  |---- JwtTokenUtil.java
|  |  |  |  |  |-[D] order
|  |  |  |  |  |  `---- OrderParams.java
|  |  |  |  |  |---- PasswordUtil.java
|  |  |  |  |  |---- QueryUtils.java
|  |  |  |  |  |---- RatingAndReviewFileAppender.java
|  |  |  |  |  |-[D] search
|  |  |  |  |  |  |---- PaginationSearchParams.java
|  |  |  |  |  |  `---- PaginationSearchResult.java
|  |  |  |  |  `---- SecureKey.java
|  |  `---- swagger.json
|  |-[D] properties
|  |  |---- IoTSMP.properties
|  |  |---- log4j.properties
|  |  `---- Messages_en.properties
|  |-[D] resources
|  |  `---- ojdbc8-12.2.0.1.jar
|  |-[D] test
|  |  `-[D] java
|  `-[D] webapp
|  |  |-[D] cors
|  |  |  |---- postmessage.html
|  |  |  `---- result.html
|  |  |-[D] fonts
|  |  |  |---- glyphicons-halflings-regular.eot
|  |  |  |---- glyphicons-halflings-regular.svg
|  |  |  |---- glyphicons-halflings-regular.ttf
|  |  |  |---- glyphicons-halflings-regular.woff
|  |  |  |---- glyphicons-halflings-regular.woff2
|  |  |  |---- Gotham-Book.eot
|  |  |  |---- Gotham-Book.ttf
|  |  |  |---- Gotham-Book.woff
|  |  |  |---- Gotham-Medium.eot
|  |  |  |---- Gotham-Medium.ttf
|  |  |  |---- Gotham-Medium.woff
|  |  |  |---- Gotham-Thin.eot
|  |  |  |---- Gotham-Thin.ttf
|  |  |  |---- Gotham-Thin.woff
|  |  |  |---- icomoon.eot
|  |  |  |---- icomoon.svg
|  |  |  |---- icomoon.ttf
|  |  |  `---- icomoon.woff
|  |  |-[D] META-INF
|  |  |  `---- MANIFEST.MF
|  |  `-[D] WEB-INF
|  |  |  `-[D] templates
|  |  |  |  `---- login.html
|-[D] target
|  |-[D] classes
|  |  |-[D] com
|  |  |  `-[D] rating
|  |  |  |  |-[D] bo
|  |  |  |  |  |---- ApiUser.class
|  |  |  |  |  |-[D] common
|  |  |  |  |  |  `---- BaseEntity.class
|  |  |  |  |  |---- RatingAndReview.class
|  |  |  |  |  |---- TokenManager.class
|  |  |  |  |  |---- User.class
|  |  |  |  |  `---- UserDetails.class
|  |  |  |  |-[D] business
|  |  |  |  |  `-[D] logic
|  |  |  |  |  |  |---- ApiUserService.class
|  |  |  |  |  |  |---- AuthService.class
|  |  |  |  |  |  |---- CommonService.class
|  |  |  |  |  |  |---- CrudService.class
|  |  |  |  |  |  |---- CustomUserDetailsService.class
|  |  |  |  |  |  |---- EncryptionDecryptionService.class
|  |  |  |  |  |  |-[D] impl
|  |  |  |  |  |  |  |---- ApiUserServiceImpl$1.class
|  |  |  |  |  |  |  |---- ApiUserServiceImpl.class
|  |  |  |  |  |  |  |---- AuthServiceImpl.class
|  |  |  |  |  |  |  |---- CommonServiceImpl.class
|  |  |  |  |  |  |  |---- CrudServiceImpl.class
|  |  |  |  |  |  |  |---- CustomUserDetailsServiceImpl.class
|  |  |  |  |  |  |  |---- EncryptionDecryptionServiceImpl.class
|  |  |  |  |  |  |  |---- HibernateUnproxyAccessHook.class
|  |  |  |  |  |  |  |---- RatingAndReviewServiceImpl$1.class
|  |  |  |  |  |  |  |---- RatingAndReviewServiceImpl.class
|  |  |  |  |  |  |  |---- SessionValidationServiceImpl.class
|  |  |  |  |  |  |  |---- TokenManagerServiceImpl.class
|  |  |  |  |  |  |  |---- TokenValidationServiceImpl.class
|  |  |  |  |  |  |  |---- UserServiceImpl$1.class
|  |  |  |  |  |  |  `---- UserServiceImpl.class
|  |  |  |  |  |  |---- IoTSecurityEventListener.class
|  |  |  |  |  |  |---- RatingAndReviewService.class
|  |  |  |  |  |  |---- ServiceCommonUtils.class
|  |  |  |  |  |  |---- SessionValidationService.class
|  |  |  |  |  |  |---- TokenManagerService.class
|  |  |  |  |  |  |---- TokenValidationService.class
|  |  |  |  |  |  `---- UserService.class
|  |  |  |  |-[D] config
|  |  |  |  |  |---- EncodingFilter.class
|  |  |  |  |  |-[D] gson
|  |  |  |  |  |  |---- HibernateProxyTypeAdapter$1.class
|  |  |  |  |  |  `---- HibernateProxyTypeAdapter.class
|  |  |  |  |  |---- SecurityConfig.class
|  |  |  |  |  |---- ServletConfig.class
|  |  |  |  |  |---- SpringMvcInitializer.class
|  |  |  |  |  |---- SpringSecurityInitializer.class
|  |  |  |  |  `---- WebSocketConfig.class
|  |  |  |  |-[D] dl
|  |  |  |  |  |---- CrudRepository$QueryFilter.class
|  |  |  |  |  |---- CrudRepository.class
|  |  |  |  |  |---- QueryFilterTemplate.class
|  |  |  |  |  |---- Repository$ComplexQueryAttribute.class
|  |  |  |  |  |---- Repository$ComplexQueryMode.class
|  |  |  |  |  |---- Repository$MatchModeData.class
|  |  |  |  |  |---- Repository$QueryAttribute.class
|  |  |  |  |  |---- Repository$QueryMode.class
|  |  |  |  |  |---- Repository$QueryParameters.class
|  |  |  |  |  |---- Repository$ResultSet.class
|  |  |  |  |  |---- Repository$SortAttribute$SortMode.class
|  |  |  |  |  |---- Repository$SortAttribute.class
|  |  |  |  |  `---- Repository.class
|  |  |  |  |-[D] dto
|  |  |  |  |  |---- ApiResponse.class
|  |  |  |  |  |---- ApiUserDTO.class
|  |  |  |  |  |---- AuthRequest.class
|  |  |  |  |  |---- AuthResponse.class
|  |  |  |  |  |---- BaseResponse.class
|  |  |  |  |  |-[D] common
|  |  |  |  |  |  `---- AbstractItem.class
|  |  |  |  |  |---- DataTableRequestDto$ColumnParameterDto.class
|  |  |  |  |  |---- DataTableRequestDto$SearchParameterDto.class
|  |  |  |  |  |---- DataTableRequestDto.class
|  |  |  |  |  |---- LoggedInUserDto.class
|  |  |  |  |  |---- ReactApiUserDto.class
|  |  |  |  |  |---- ReactUserDto.class
|  |  |  |  |  `---- UserDTO.class
|  |  |  |  |-[D] enums
|  |  |  |  |  |---- Error.class
|  |  |  |  |  `---- IdDisplayNameProvidable.class
|  |  |  |  |-[D] exc
|  |  |  |  |  |---- ApiErrorDto.class
|  |  |  |  |  |---- ApiException.class
|  |  |  |  |  |---- ApiFieldErrorDto.class
|  |  |  |  |  |---- FieldErrorDto.class
|  |  |  |  |  `---- RatingAndReviewException.class
|  |  |  |  |-[D] interceptors
|  |  |  |  |  |---- APIAccessValidationInterceptor.class
|  |  |  |  |  |---- RequestWrapper.class
|  |  |  |  |  |---- SessionValidationInterceptor.class
|  |  |  |  |  |---- TokenValidationInterceptor.class
|  |  |  |  |  |---- XSSRequestWrapper$ResettableServletInputStream.class
|  |  |  |  |  `---- XSSRequestWrapper.class
|  |  |  |  |-[D] react
|  |  |  |  |  `-[D] web
|  |  |  |  |  |  `-[D] controller
|  |  |  |  |  |  |  |---- AdminViewController.class
|  |  |  |  |  |  |  |---- ApiController.class
|  |  |  |  |  |  |  |---- PasswrdController.class
|  |  |  |  |  |  |  |---- RatingAndReviewController.class
|  |  |  |  |  |  |  `---- SignInController.class
|  |  |  |  |-[D] response
|  |  |  |  |  `---- Message.class
|  |  |  |  |-[D] serializers
|  |  |  |  |  `---- IdDisplayNameSerializer.class
|  |  |  |  |-[D] transform
|  |  |  |  |  |---- RatingAndReviewFieldMapper.class
|  |  |  |  |  |---- ResponseTransformer.class
|  |  |  |  |  |---- TransformProcessor.class
|  |  |  |  |  `---- TransformProcessorImpl.class
|  |  |  |  `-[D] utils
|  |  |  |  |  |---- CommonUtils.class
|  |  |  |  |  |-[D] datatable
|  |  |  |  |  |  |---- ColumnParameter.class
|  |  |  |  |  |  |---- DataTableResponse.class
|  |  |  |  |  |  |---- DataTablesInput.class
|  |  |  |  |  |  |---- OrderParameter.class
|  |  |  |  |  |  `---- SearchParameter.class
|  |  |  |  |  |---- Encryption.class
|  |  |  |  |  |---- JwtApiTokenUtil.class
|  |  |  |  |  |---- JwtTokenUtil.class
|  |  |  |  |  |-[D] order
|  |  |  |  |  |  `---- OrderParams.class
|  |  |  |  |  |---- PasswordUtil.class
|  |  |  |  |  |---- QueryUtils.class
|  |  |  |  |  |---- RatingAndReviewFileAppender$1.class
|  |  |  |  |  |---- RatingAndReviewFileAppender.class
|  |  |  |  |  |-[D] search
|  |  |  |  |  |  |---- PaginationSearchParams.class
|  |  |  |  |  |  |---- PaginationSearchResult$Builder.class
|  |  |  |  |  |  `---- PaginationSearchResult.class
|  |  |  |  |  `---- SecureKey.class
|  |  |---- log4j.properties
|  |  |---- Messages_en.properties
|  |  |---- ojdbc8-12.2.0.1.jar
|  |  `---- swagger.json
|  |-[D] GControl
|  |  |-[D] cors
|  |  |  |---- postmessage.html
|  |  |  `---- result.html
|  |  |-[D] fonts
|  |  |  |---- glyphicons-halflings-regular.eot
|  |  |  |---- glyphicons-halflings-regular.svg
|  |  |  |---- glyphicons-halflings-regular.ttf
|  |  |  |---- glyphicons-halflings-regular.woff
|  |  |  |---- glyphicons-halflings-regular.woff2
|  |  |  |---- Gotham-Book.eot
|  |  |  |---- Gotham-Book.ttf
|  |  |  |---- Gotham-Book.woff
|  |  |  |---- Gotham-Medium.eot
|  |  |  |---- Gotham-Medium.ttf
|  |  |  |---- Gotham-Medium.woff
|  |  |  |---- Gotham-Thin.eot
|  |  |  |---- Gotham-Thin.ttf
|  |  |  |---- Gotham-Thin.woff
|  |  |  |---- icomoon.eot
|  |  |  |---- icomoon.svg
|  |  |  |---- icomoon.ttf
|  |  |  `---- icomoon.woff
|  |  |-[D] META-INF
|  |  |  `---- MANIFEST.MF
|  |  `-[D] WEB-INF
|  |  |  |-[D] classes
|  |  |  |  |---- log4j.properties
|  |  |  |  |---- Messages_en.properties
|  |  |  |  `---- ojdbc8-12.2.0.1.jar
|  |  |  |-[D] lib
|  |  |  |  |---- activation-1.1.jar
|  |  |  |  |---- antlr-2.7.7.jar
|  |  |  |  |---- aopalliance-1.0.jar
|  |  |  |  |---- aspectjrt-1.8.9.jar
|  |  |  |  |---- aspectjweaver-1.8.9.jar
|  |  |  |  |---- attoparser-2.0.2.RELEASE.jar
|  |  |  |  |---- boxable-1.4.jar
|  |  |  |  |---- c3p0-0.9.0.4.jar
|  |  |  |  |---- classmate-1.3.4.jar
|  |  |  |  |---- com.ibm.jbatch-tck-spi-1.0.jar
|  |  |  |  |---- commons-beanutils-1.8.3.jar
|  |  |  |  |---- commons-chain-1.1.jar
|  |  |  |  |---- commons-codec-1.9.jar
|  |  |  |  |---- commons-collections-3.2.2.jar
|  |  |  |  |---- commons-collections4-4.2.jar
|  |  |  |  |---- commons-csv-1.2.jar
|  |  |  |  |---- commons-digester-1.8.jar
|  |  |  |  |---- commons-fileupload-1.3.2.jar
|  |  |  |  |---- commons-io-2.5.jar
|  |  |  |  |---- commons-lang-2.4.jar
|  |  |  |  |---- commons-lang3-3.4.jar
|  |  |  |  |---- commons-logging-1.2.jar
|  |  |  |  |---- commons-text-1.3.jar
|  |  |  |  |---- commons-validator-1.3.1.jar
|  |  |  |  |---- dom4j-1.6.1.jar
|  |  |  |  |---- dozer-5.4.0.jar
|  |  |  |  |---- ehcache-core-2.4.3.jar
|  |  |  |  |---- fast-classpath-scanner-2.4.7.jar
|  |  |  |  |---- fontbox-2.0.1.jar
|  |  |  |  |---- groovy-2.4.6.jar
|  |  |  |  |---- gson-2.2.2.jar
|  |  |  |  |---- guava-20.0.jar
|  |  |  |  |---- hibernate-commons-annotations-4.0.2.Final.jar
|  |  |  |  |---- hibernate-core-4.2.8.Final.jar
|  |  |  |  |---- hibernate-ehcache-4.2.8.Final.jar
|  |  |  |  |---- hibernate-jpa-2.0-api-1.0.1.Final.jar
|  |  |  |  |---- hibernate-validator-6.1.0.Final.jar
|  |  |  |  |---- httpclient-4.5.2.jar
|  |  |  |  |---- httpcore-4.4.4.jar
|  |  |  |  |---- itextpdf-5.5.10.jar
|  |  |  |  |---- jackson-annotations-2.8.4.jar
|  |  |  |  |---- jackson-core-2.8.4.jar
|  |  |  |  |---- jackson-core-asl-1.9.13.jar
|  |  |  |  |---- jackson-databind-2.8.4.jar
|  |  |  |  |---- jackson-mapper-asl-1.9.13.jar
|  |  |  |  |---- jakarta.activation-api-1.2.1.jar
|  |  |  |  |---- jakarta.validation-api-2.0.2.jar
|  |  |  |  |---- jakarta.xml.bind-api-2.3.2.jar
|  |  |  |  |---- javassist-3.18.1-GA.jar
|  |  |  |  |---- javax.batch-api-1.0.jar
|  |  |  |  |---- javax.persistence-api-2.2.jar
|  |  |  |  |---- javax.servlet-api-3.1.0.jar
|  |  |  |  |---- javers-core-3.6.3.jar
|  |  |  |  |---- javers-persistence-sql-5.1.3.jar
|  |  |  |  |---- javers-spring-5.1.3.jar
|  |  |  |  |---- javers-spring-boot-starter-sql-5.1.3.jar
|  |  |  |  |---- javers-spring-jpa-5.1.3.jar
|  |  |  |  |---- jboss-logging-3.1.0.GA.jar
|  |  |  |  |---- jboss-transaction-api_1.1_spec-1.0.1.Final.jar
|  |  |  |  |---- jboss-transaction-api_1.2_spec-1.0.0.Final.jar
|  |  |  |  |---- jcl-over-slf4j-1.7.21.jar
|  |  |  |  |---- jettison-1.2.jar
|  |  |  |  |---- jjwt-0.7.0.jar
|  |  |  |  |---- jsch-0.1.52.jar
|  |  |  |  |---- json-simple-1.1.1.jar
|  |  |  |  |---- log4j-1.2.15.jar
|  |  |  |  |---- mail-1.4.jar
|  |  |  |  |---- mapstruct-1.0.0.Final.jar
|  |  |  |  |---- mysql-connector-java-8.0.15.jar
|  |  |  |  |---- ognl-3.1.12.jar
|  |  |  |  |---- opencsv-4.2.jar
|  |  |  |  |---- oro-2.0.8.jar
|  |  |  |  |---- pdfbox-2.0.1.jar
|  |  |  |  |---- picocontainer-2.15.jar
|  |  |  |  |---- polyjdbc-0.7.5.jar
|  |  |  |  |---- protobuf-java-2.6.0.jar
|  |  |  |  |---- reactive-streams-1.0.0.jar
|  |  |  |  |---- slf4j-api-1.6.1.jar
|  |  |  |  |---- slf4j-log4j12-1.6.1.jar
|  |  |  |  |---- spring-aop-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-aspects-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-batch-core-3.0.7.RELEASE.jar
|  |  |  |  |---- spring-batch-infrastructure-3.0.7.RELEASE.jar
|  |  |  |  |---- spring-beans-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-context-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-context-support-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-core-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-data-commons-1.12.4.RELEASE.jar
|  |  |  |  |---- spring-data-jpa-1.10.4.RELEASE.jar
|  |  |  |  |---- spring-expression-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-integration-core-4.3.4.RELEASE.jar
|  |  |  |  |---- spring-integration-file-4.2.5.RELEASE.jar
|  |  |  |  |---- spring-integration-java-dsl-1.2.0.RELEASE.jar
|  |  |  |  |---- spring-integration-sftp-4.2.5.RELEASE.jar
|  |  |  |  |---- spring-integration-stream-4.2.5.RELEASE.jar
|  |  |  |  |---- spring-jdbc-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-messaging-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-orm-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-plugin-core-1.2.0.RELEASE.jar
|  |  |  |  |---- spring-plugin-metadata-1.2.0.RELEASE.jar
|  |  |  |  |---- spring-retry-1.1.3.RELEASE.jar
|  |  |  |  |---- spring-security-config-4.1.3.RELEASE.jar
|  |  |  |  |---- spring-security-core-4.1.3.RELEASE.jar
|  |  |  |  |---- spring-security-web-4.1.3.RELEASE.jar
|  |  |  |  |---- spring-tx-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-web-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-webmvc-4.3.3.RELEASE.jar
|  |  |  |  |---- spring-websocket-4.3.3.RELEASE.jar
|  |  |  |  |---- springfox-core-2.5.0.jar
|  |  |  |  |---- springfox-schema-2.5.0.jar
|  |  |  |  |---- springfox-spi-2.5.0.jar
|  |  |  |  |---- springfox-spring-web-2.5.0.jar
|  |  |  |  |---- springfox-swagger-common-2.5.0.jar
|  |  |  |  |---- springfox-swagger-ui-2.5.0.jar
|  |  |  |  |---- springfox-swagger2-2.5.0.jar
|  |  |  |  |---- sslext-1.2-0.jar
|  |  |  |  |---- struts-core-1.3.8.jar
|  |  |  |  |---- struts-taglib-1.3.8.jar
|  |  |  |  |---- struts-tiles-1.3.8.jar
|  |  |  |  |---- swagger-annotations-1.5.9.jar
|  |  |  |  |---- swagger-models-1.5.9.jar
|  |  |  |  |---- thymeleaf-3.0.3.RELEASE.jar
|  |  |  |  |---- thymeleaf-expression-processor-1.1.2.jar
|  |  |  |  |---- thymeleaf-extras-springsecurity4-3.0.0.RELEASE.jar
|  |  |  |  |---- thymeleaf-extras-tiles2-2.1.1.RELEASE.jar
|  |  |  |  |---- thymeleaf-layout-dialect-2.2.0.jar
|  |  |  |  |---- thymeleaf-spring4-3.0.3.RELEASE.jar
|  |  |  |  |---- tiles-api-2.2.2.jar
|  |  |  |  |---- tiles-core-2.2.2.jar
|  |  |  |  |---- tiles-jsp-2.2.2.jar
|  |  |  |  |---- tiles-servlet-2.2.2.jar
|  |  |  |  |---- tiles-template-2.2.2.jar
|  |  |  |  |---- unbescape-1.1.4.RELEASE.jar
|  |  |  |  |---- velocity-1.7.jar
|  |  |  |  |---- velocity-tools-2.0.jar
|  |  |  |  |---- xmlpull-1.1.3.1.jar
|  |  |  |  |---- xmlworker-5.5.10.jar
|  |  |  |  |---- xpp3_min-1.1.4c.jar
|  |  |  |  `---- xstream-1.4.7.jar
|  |  |  `-[D] templates
|  |  |  |  `---- login.html
|  |-[D] generated-test-sources
|  |  `-[D] test-annotations
|  |-[D] m2e-wtp
|  |  `-[D] web-resources
|  |  |  `-[D] META-INF
|  |  |  |  |---- MANIFEST.MF
|  |  |  |  `-[D] maven
|  |  |  |  |  `-[D] RaitingReview
|  |  |  |  |  |  `-[D] RaitingAndReview
|  |  |  |  |  |  |  |---- pom.properties
|  |  |  |  |  |  |  `---- pom.xml
|  |-[D] maven-archiver
|  |  `---- pom.properties
|  |-[D] maven-status
|  |  `-[D] maven-compiler-plugin
|  |  |  `-[D] testCompile
|  |  |  |  `-[D] default-testCompile
|  |  |  |  |  `---- inputFiles.lst
|  |---- RaitingAndReview.war
|  `-[D] test-classes
`-[D] WebContent
|  |---- index.jsp
|  |-[D] META-INF
|  |  `---- MANIFEST.MF
|  `-[D] WEB-INF
|  |  |-[D] lib
|  |  `---- web.xml
`





There will be on property file  IoTSMP.properties which contains all Project related Congiguration. 
