# Project Tree

`	ext
Folder PATH listing
Volume serial number is 0000001C BA0A:9B28
C:.
|   .gitattributes
|   .gitignore
|   DELIVERABLES_MANIFEST.md
|   ERROR_CHECK_AND_BUILD_REPORT.md
|   FINAL_IMPLEMENTATION_SUMMARY.md
|   IMPLEMENTATION_COMPLETE.sh
|   mvnw
|   mvnw.cmd
|   PHASE2_COMPLETION.md
|   PHASE3_BUILD_SUMMARY.md
|   PHASE3_COMPLETION.md
|   PHASE3_EXECUTIVE_SUMMARY.md
|   pom.xml
|   POSTMAN_TESTING_GUIDE.md
|   project-tree.md
|   README.md
|   REFACTORING_SUMMARY.md
|   REFACTORING_VERIFICATION_REPORT.md
|   SPRING_JPA_AUDITING_IMPLEMENTATION.md
|   Teams-API-Postman-Collection.json
|   
+---.github
|   |   copilot-instructions.md
|   |   
|   +---agents
|   |       speckit.analyze.agent.md
|   |       speckit.checklist.agent.md
|   |       speckit.clarify.agent.md
|   |       speckit.constitution.agent.md
|   |       speckit.git.commit.agent.md
|   |       speckit.git.feature.agent.md
|   |       speckit.git.initialize.agent.md
|   |       speckit.git.remote.agent.md
|   |       speckit.git.validate.agent.md
|   |       speckit.implement.agent.md
|   |       speckit.plan.agent.md
|   |       speckit.specify.agent.md
|   |       speckit.tasks.agent.md
|   |       speckit.taskstoissues.agent.md
|   |       
|   \---prompts
|           speckit.analyze.prompt.md
|           speckit.checklist.prompt.md
|           speckit.clarify.prompt.md
|           speckit.constitution.prompt.md
|           speckit.git.commit.prompt.md
|           speckit.git.feature.prompt.md
|           speckit.git.initialize.prompt.md
|           speckit.git.remote.prompt.md
|           speckit.git.validate.prompt.md
|           speckit.implement.prompt.md
|           speckit.plan.prompt.md
|           speckit.specify.prompt.md
|           speckit.tasks.prompt.md
|           speckit.taskstoissues.prompt.md
|           
+---.idea
|   |   .gitignore
|   |   compiler.xml
|   |   copilotDiffState.xml
|   |   dataSources.local.xml
|   |   dataSources.xml
|   |   encodings.xml
|   |   jarRepositories.xml
|   |   material_theme_project_new.xml
|   |   misc.xml
|   |   sqldialects.xml
|   |   vcs.xml
|   |   workspace.xml
|   |   
|   +---dataSources
|   |   |   bccdfa3f-e15b-4371-a1fc-0a5f88d216d2.xml
|   |   |   data_sources_history.xml
|   |   |   
|   |   \---bccdfa3f-e15b-4371-a1fc-0a5f88d216d2
|   |       \---storage_v2
|   |           \---_src_
|   |               \---database
|   |                   |   postgres.edMnLQ.meta
|   |                   |   
|   |                   \---postgres.edMnLQ
|   |                       \---schema
|   |                               information_schema.FNRwLQ.meta
|   |                               pg_catalog.0S1ZNQ.meta
|   |                               public.abK9xQ.meta
|   |                               
|   \---shelf
|       |   Uncommitted_changes_before_rebase__Changes_.xml
|       |   
|       \---Uncommitted_changes_before_rebase_[Changes]
|               shelved.patch
|               
+---.mvn
|   \---wrapper
|           maven-wrapper.properties
|           
+---.postman
+---.specify
|   |   extensions.yml
|   |   IMPLEMENTATION_COMPLETE.md
|   |   init-options.json
|   |   integration.json
|   |   PHASE_1_IMPLEMENTATION_SUMMARY.md
|   |   PHASE_2_IMPLEMENTATION_SUMMARY.md
|   |   PHASE_2_TESTING_GUIDE.md
|   |   PHASE_3_IMPLEMENTATION.md
|   |   plan-microsoftTeamsLikePlatform.prompt.md
|   |   
|   +---extensions
|   |   |   .registry
|   |   |   
|   |   \---git
|   |       |   config-template.yml
|   |       |   extension.yml
|   |       |   git-config.yml
|   |       |   README.md
|   |       |   
|   |       +---commands
|   |       |       speckit.git.commit.md
|   |       |       speckit.git.feature.md
|   |       |       speckit.git.initialize.md
|   |       |       speckit.git.remote.md
|   |       |       speckit.git.validate.md
|   |       |       
|   |       \---scripts
|   |           +---bash
|   |           |       auto-commit.sh
|   |           |       create-new-feature.sh
|   |           |       git-common.sh
|   |           |       initialize-repo.sh
|   |           |       
|   |           \---powershell
|   |                   auto-commit.ps1
|   |                   create-new-feature.ps1
|   |                   git-common.ps1
|   |                   initialize-repo.ps1
|   |                   
|   +---integrations
|   |       copilot.manifest.json
|   |       speckit.manifest.json
|   |       
|   +---memory
|   |       constitution.md
|   |       
|   +---scripts
|   |   \---powershell
|   |           check-prerequisites.ps1
|   |           common.ps1
|   |           create-new-feature.ps1
|   |           setup-plan.ps1
|   |           setup-tasks.ps1
|   |           
|   +---specs
|   |       microsoft_teams_like_platform_sow_brs.md
|   |       
|   +---templates
|   |       checklist-template.md
|   |       constitution-template.md
|   |       plan-template.md
|   |       spec-template.md
|   |       tasks-template.md
|   |       
|   \---workflows
|       |   workflow-registry.json
|       |   
|       \---speckit
|               workflow.yml
|               
+---postman
|   +---collections
|   +---environments
|   +---flows
|   +---globals
|   +---mocks
|   \---specs
+---src
|   +---main
|   |   +---java
|   |   |   \---com
|   |   |       \---teams
|   |   |           \---teams
|   |   |               |   TeamsApplication.java
|   |   |               |   
|   |   |               +---config
|   |   |               |       AuditorAwareImpl.java
|   |   |               |       FlywayRunner.java
|   |   |               |       JpaAuditingConfig.java
|   |   |               |       OpenApiConfig.java
|   |   |               |       PasswordEncoderConfig.java
|   |   |               |       SecurityConfig.java
|   |   |               |       
|   |   |               +---controller
|   |   |               |       AttachmentController.java
|   |   |               |       AuditLogController.java
|   |   |               |       AuthController.java
|   |   |               |       ChannelController.java
|   |   |               |       HealthController.java
|   |   |               |       MessageController.java
|   |   |               |       NotificationController.java
|   |   |               |       TeamController.java
|   |   |               |       UserController.java
|   |   |               |       
|   |   |               +---domain
|   |   |               |       Attachment.java
|   |   |               |       AuditLog.java
|   |   |               |       BaseEntity.java
|   |   |               |       Channel.java
|   |   |               |       Conversation.java
|   |   |               |       Message.java
|   |   |               |       MessageReaction.java
|   |   |               |       MessageRead.java
|   |   |               |       Notification.java
|   |   |               |       NotificationType.java
|   |   |               |       Permission.java
|   |   |               |       Role.java
|   |   |               |       Team.java
|   |   |               |       TeamMember.java
|   |   |               |       TeamMemberRole.java
|   |   |               |       User.java
|   |   |               |       
|   |   |               +---dto
|   |   |               |       ApiResponse.java
|   |   |               |       AttachmentDto.java
|   |   |               |       AuditLogDto.java
|   |   |               |       AuthResponse.java
|   |   |               |       ChannelDto.java
|   |   |               |       CreateChannelRequest.java
|   |   |               |       CreateMessageRequest.java
|   |   |               |       CreateTeamRequest.java
|   |   |               |       LoginRequest.java
|   |   |               |       MessageDto.java
|   |   |               |       NotificationDto.java
|   |   |               |       RegisterRequest.java
|   |   |               |       TeamDto.java
|   |   |               |       TeamMemberDto.java
|   |   |               |       UpdateChannelRequest.java
|   |   |               |       UpdateMessageRequest.java
|   |   |               |       UpdateTeamRequest.java
|   |   |               |       UserDto.java
|   |   |               |       
|   |   |               +---exception
|   |   |               |       BadRequestException.java
|   |   |               |       GlobalExceptionHandler.java
|   |   |               |       ResourceNotFoundException.java
|   |   |               |       UnauthorizedException.java
|   |   |               |       
|   |   |               +---repository
|   |   |               |       AttachmentRepository.java
|   |   |               |       AuditLogRepository.java
|   |   |               |       ChannelRepository.java
|   |   |               |       ConversationRepository.java
|   |   |               |       MessageRepository.java
|   |   |               |       NotificationRepository.java
|   |   |               |       PermissionRepository.java
|   |   |               |       RoleRepository.java
|   |   |               |       TeamMemberRepository.java
|   |   |               |       TeamRepository.java
|   |   |               |       UserRepository.java
|   |   |               |       
|   |   |               +---security
|   |   |               |       CustomUserDetailsService.java
|   |   |               |       JwtAuthenticationFilter.java
|   |   |               |       JwtTokenProvider.java
|   |   |               |       TeamSecurityService.java
|   |   |               |       
|   |   |               \---service
|   |   |                   |   AttachmentService.java
|   |   |                   |   AuditLogService.java
|   |   |                   |   AuthService.java
|   |   |                   |   ChannelService.java
|   |   |                   |   CurrentUserService.java
|   |   |                   |   MessageService.java
|   |   |                   |   NotificationService.java
|   |   |                   |   TeamService.java
|   |   |                   |   UserService.java
|   |   |                   |   
|   |   |                   \---impl
|   |   |                           AttachmentServiceImpl.java
|   |   |                           AuditLogServiceImpl.java
|   |   |                           AuthServiceImpl.java
|   |   |                           ChannelServiceImpl.java
|   |   |                           MessageServiceImpl.java
|   |   |                           NotificationServiceImpl.java
|   |   |                           TeamServiceImpl.java
|   |   |                           UserServiceImpl.java
|   |   |                           
|   |   \---resources
|   |       |   application.properties
|   |       |   
|   |       \---db
|   |           \---migration
|   |                   V1__Initial_Schema.sql
|   |                   V2__Insert_Roles_And_Permissions.sql
|   |                   V3__Add_Spring_JPA_Auditing_Columns.sql
|   |                   V4__Complete_BaseEntity_Soft_Delete_Columns.sql
|   |                   V5__Convert_Team_Members_To_Role_Table.sql
|   |                   V6__Fix_Missing_Audit_Columns.sql
|   |                   V7__Add_message_reads_created_at.sql
|   |                   
|   \---test
|       \---java
|           \---com
|               \---teams
|                   \---teams
|                           TeamsApplicationTests.java
|                           
\---target
    |   Teams-0.0.1-SNAPSHOT.jar
    |   Teams-0.0.1-SNAPSHOT.jar.original
    |   
    +---classes
    |   |   application.properties
    |   |   
    |   +---com
    |   |   \---teams
    |   |       \---teams
    |   |           |   TeamsApplication.class
    |   |           |   
    |   |           +---config
    |   |           |       AuditorAwareImpl.class
    |   |           |       FlywayRunner.class
    |   |           |       JpaAuditingConfig.class
    |   |           |       OpenApiConfig.class
    |   |           |       PasswordEncoderConfig.class
    |   |           |       SecurityConfig.class
    |   |           |       
    |   |           +---controller
    |   |           |       AttachmentController.class
    |   |           |       AuditLogController.class
    |   |           |       AuthController.class
    |   |           |       ChannelController.class
    |   |           |       HealthController.class
    |   |           |       MessageController.class
    |   |           |       NotificationController.class
    |   |           |       TeamController.class
    |   |           |       UserController.class
    |   |           |       
    |   |           +---domain
    |   |           |       Attachment$AttachmentBuilder.class
    |   |           |       Attachment.class
    |   |           |       AuditLog$AuditLogBuilder.class
    |   |           |       AuditLog.class
    |   |           |       BaseEntity.class
    |   |           |       Channel$ChannelBuilder.class
    |   |           |       Channel.class
    |   |           |       Conversation$ConversationBuilder.class
    |   |           |       Conversation.class
    |   |           |       Message$MessageBuilder.class
    |   |           |       Message.class
    |   |           |       MessageReaction$MessageReactionBuilder.class
    |   |           |       MessageReaction.class
    |   |           |       MessageRead$MessageReadBuilder.class
    |   |           |       MessageRead.class
    |   |           |       Notification$NotificationBuilder.class
    |   |           |       Notification.class
    |   |           |       NotificationType.class
    |   |           |       Permission$PermissionType.class
    |   |           |       Permission.class
    |   |           |       Role$RoleType.class
    |   |           |       Role.class
    |   |           |       Team$TeamBuilder.class
    |   |           |       Team.class
    |   |           |       TeamMember$TeamMemberBuilder.class
    |   |           |       TeamMember.class
    |   |           |       TeamMemberRole.class
    |   |           |       User$UserBuilder.class
    |   |           |       User.class
    |   |           |       
    |   |           +---dto
    |   |           |       ApiResponse$ApiResponseBuilder.class
    |   |           |       ApiResponse$ErrorDetails.class
    |   |           |       ApiResponse.class
    |   |           |       AttachmentDto$AttachmentDtoBuilder.class
    |   |           |       AttachmentDto.class
    |   |           |       AuditLogDto$AuditLogDtoBuilder.class
    |   |           |       AuditLogDto.class
    |   |           |       AuthResponse$AuthResponseBuilder.class
    |   |           |       AuthResponse.class
    |   |           |       ChannelDto$ChannelDtoBuilder.class
    |   |           |       ChannelDto.class
    |   |           |       CreateChannelRequest$CreateChannelRequestBuilder.class
    |   |           |       CreateChannelRequest.class
    |   |           |       CreateMessageRequest$CreateMessageRequestBuilder.class
    |   |           |       CreateMessageRequest.class
    |   |           |       CreateTeamRequest$CreateTeamRequestBuilder.class
    |   |           |       CreateTeamRequest.class
    |   |           |       LoginRequest.class
    |   |           |       MessageDto$MessageDtoBuilder.class
    |   |           |       MessageDto.class
    |   |           |       NotificationDto$NotificationDtoBuilder.class
    |   |           |       NotificationDto.class
    |   |           |       RegisterRequest.class
    |   |           |       TeamDto$TeamDtoBuilder.class
    |   |           |       TeamDto.class
    |   |           |       TeamMemberDto$TeamMemberDtoBuilder.class
    |   |           |       TeamMemberDto.class
    |   |           |       UpdateChannelRequest$UpdateChannelRequestBuilder.class
    |   |           |       UpdateChannelRequest.class
    |   |           |       UpdateMessageRequest$UpdateMessageRequestBuilder.class
    |   |           |       UpdateMessageRequest.class
    |   |           |       UpdateTeamRequest$UpdateTeamRequestBuilder.class
    |   |           |       UpdateTeamRequest.class
    |   |           |       UserDto$UserDtoBuilder.class
    |   |           |       UserDto.class
    |   |           |       
    |   |           +---exception
    |   |           |       BadRequestException.class
    |   |           |       GlobalExceptionHandler.class
    |   |           |       ResourceNotFoundException.class
    |   |           |       UnauthorizedException.class
    |   |           |       
    |   |           +---repository
    |   |           |       AttachmentRepository.class
    |   |           |       AuditLogRepository.class
    |   |           |       ChannelRepository.class
    |   |           |       ConversationRepository.class
    |   |           |       MessageRepository.class
    |   |           |       NotificationRepository.class
    |   |           |       PermissionRepository.class
    |   |           |       RoleRepository.class
    |   |           |       TeamMemberRepository.class
    |   |           |       TeamRepository.class
    |   |           |       UserRepository.class
    |   |           |       
    |   |           +---security
    |   |           |       CustomUserDetailsService.class
    |   |           |       JwtAuthenticationFilter.class
    |   |           |       JwtTokenProvider.class
    |   |           |       TeamSecurityService.class
    |   |           |       
    |   |           \---service
    |   |               |   AttachmentService.class
    |   |               |   AuditLogService.class
    |   |               |   AuthService.class
    |   |               |   ChannelService.class
    |   |               |   CurrentUserService.class
    |   |               |   MessageService.class
    |   |               |   NotificationService.class
    |   |               |   TeamService.class
    |   |               |   UserService.class
    |   |               |   
    |   |               \---impl
    |   |                       AttachmentServiceImpl.class
    |   |                       AuditLogServiceImpl.class
    |   |                       AuthServiceImpl.class
    |   |                       ChannelServiceImpl.class
    |   |                       MessageServiceImpl.class
    |   |                       NotificationServiceImpl.class
    |   |                       TeamServiceImpl.class
    |   |                       UserServiceImpl.class
    |   |                       
    |   \---db
    |       \---migration
    |               V1__Initial_Schema.sql
    |               V2__Insert_Roles_And_Permissions.sql
    |               V3__Add_Spring_JPA_Auditing_Columns.sql
    |               V4__Complete_BaseEntity_Soft_Delete_Columns.sql
    |               V5__Convert_Team_Members_To_Role_Table.sql
    |               V6__Fix_Missing_Audit_Columns.sql
    |               V7__Add_message_reads_created_at.sql
    |               
    +---generated-sources
    |   \---annotations
    +---generated-test-sources
    |   \---test-annotations
    +---maven-archiver
    |       pom.properties
    |       
    +---maven-status
    |   \---maven-compiler-plugin
    |       +---compile
    |       |   \---default-compile
    |       |           createdFiles.lst
    |       |           inputFiles.lst
    |       |           
    |       \---testCompile
    |           \---default-testCompile
    |                   createdFiles.lst
    |                   inputFiles.lst
    |                   
    \---test-classes
        \---com
            \---teams
                \---teams
                        TeamsApplicationTests.class
                        
