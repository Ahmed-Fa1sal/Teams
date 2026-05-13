# Phase 2 Testing Guide

## Prerequisites

1. Ensure PostgreSQL is running
2. Application running on http://localhost:8080/api/v1
3. Generate a valid JWT token from Phase 1 (register and login)

## Test Workflow

### Step 1: Authenticate (Phase 1 - Required)
```bash
# Register a user
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "email": "testuser@example.com",
  "username": "testuser",
  "password": "TestPassword123!",
  "firstName": "Test",
  "lastName": "User"
}

# Response will contain tokens
# Save the accessToken for Phase 2 tests
```

### Step 2: Create Teams (Phase 2 - New)
```bash
# Create Team 1
POST http://localhost:8080/api/v1/teams
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "Engineering",
  "description": "Engineering team workspace",
  "imageUrl": "https://example.com/team.jpg",
  "isPublic": true
}

# Response:
{
  "code": 201,
  "message": "Resource created successfully",
  "data": {
    "id": 1,
    "name": "Engineering",
    "description": "Engineering team workspace",
    "owner": { "id": 1, "username": "testuser", ... },
    "members": [ ... ],
    "memberCount": 1,
    "channelCount": 0,
    "isPublic": true,
    "archived": false,
    "createdAt": "2026-05-13T...",
    "updatedAt": "2026-05-13T..."
  }
}
```

### Step 3: List Teams
```bash
# Get all teams (paginated)
GET http://localhost:8080/api/v1/teams?page=0&size=20
Authorization: Bearer {accessToken}

# Get current user's teams
GET http://localhost:8080/api/v1/teams/my-teams?page=0&size=20
Authorization: Bearer {accessToken}

# Search teams
GET http://localhost:8080/api/v1/teams/search?query=Engineering&page=0&size=20
Authorization: Bearer {accessToken}
```

### Step 4: Update Team
```bash
# Update Team 1
PUT http://localhost:8080/api/v1/teams/1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "description": "Updated team description",
  "imageUrl": "https://example.com/new-team.jpg"
}
```

### Step 5: Add Team Members
```bash
# Add user 2 to team 1
POST http://localhost:8080/api/v1/teams/1/members/2
Authorization: Bearer {accessToken}

# Response:
{
  "code": 200,
  "message": "Team member added successfully",
  "data": null
}
```

### Step 6: Create Channels (Phase 2 - New)
```bash
# Create general channel in team 1
POST http://localhost:8080/api/v1/channels
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "teamId": 1,
  "name": "general",
  "description": "General discussion channel",
  "isPublic": true
}

# Response will include channel details
# Save the channelId for message tests
```

### Step 7: List Channels in Team
```bash
# Get all channels in team 1
GET http://localhost:8080/api/v1/channels/team/1?page=0&size=20
Authorization: Bearer {accessToken}

# Search channels
GET http://localhost:8080/api/v1/channels/search?query=general&page=0&size=20
Authorization: Bearer {accessToken}
```

### Step 8: Update Channel
```bash
# Update general channel
PUT http://localhost:8080/api/v1/channels/1
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "description": "Updated channel description",
  "isPublic": false
}
```

### Step 9: Create Messages (Phase 2 - New)
```bash
# Create message in general channel
POST http://localhost:8080/api/v1/messages
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "content": "Hello, Engineering team! Welcome to the general channel.",
  "channelId": 1
}

# Save the messageId for reply tests
```

### Step 10: Get Channel Messages
```bash
# Get all messages in general channel (newest first)
GET http://localhost:8080/api/v1/messages/channel/1?page=0&size=20
Authorization: Bearer {accessToken}

# Response shows all messages with sender info, reply counts, etc.
```

### Step 11: Reply to Message
```bash
# Reply to message 1
POST http://localhost:8080/api/v1/messages
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "content": "Thanks for the introduction!",
  "channelId": 1,
  "replyToId": 1
}

# Save new messageId for further tests
```

### Step 12: Get Message Replies
```bash
# Get all replies to message 1
GET http://localhost:8080/api/v1/messages/1/replies?page=0&size=20
Authorization: Bearer {accessToken}

# Shows all messages that replied to message 1
```

### Step 13: Search Messages
```bash
# Search messages containing "Engineering"
GET http://localhost:8080/api/v1/messages/search?query=Engineering&page=0&size=20
Authorization: Bearer {accessToken}

# Returns messages matching search term
```

### Step 14: Edit Message
```bash
# Edit message 2 (must be the sender)
PUT http://localhost:8080/api/v1/messages/2
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "content": "Updated message content - added more details!"
}

# Note: edited flag will be set to true in response
```

### Step 15: Delete Message
```bash
# Delete message 2 (soft delete, only sender can delete)
DELETE http://localhost:8080/api/v1/messages/2
Authorization: Bearer {accessToken}

# Response:
{
  "code": 200,
  "message": "Message deleted successfully",
  "data": null
}

# Note: Message marked as deleted but not removed from DB
```

### Step 16: Archive Channel
```bash
# Archive general channel
POST http://localhost:8080/api/v1/channels/1/archive
Authorization: Bearer {accessToken}

# Response:
{
  "code": 200,
  "message": "Channel archived successfully",
  "data": null
}
```

### Step 17: Unarchive Channel
```bash
# Unarchive general channel
POST http://localhost:8080/api/v1/channels/1/unarchive
Authorization: Bearer {accessToken}
```

### Step 18: Remove Team Member
```bash
# Remove user 2 from team 1
DELETE http://localhost:8080/api/v1/teams/1/members/2
Authorization: Bearer {accessToken}
```

### Step 19: Archive Team
```bash
# Archive team 1
POST http://localhost:8080/api/v1/teams/1/archive
Authorization: Bearer {accessToken}
```

## Error Scenarios to Test

### 1. Missing Required Fields
```bash
# Try to create team without name
POST http://localhost:8080/api/v1/teams
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "description": "No name provided"
}

# Expected: 400 Bad Request
```

### 2. Team/Channel Not Found
```bash
# Try to get non-existent team
GET http://localhost:8080/api/v1/teams/9999
Authorization: Bearer {accessToken}

# Expected: 404 Not Found
```

### 3. Unauthorized Edit
```bash
# User 1 tries to edit message created by User 2
PUT http://localhost:8080/api/v1/messages/999
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "content": "Hacked!"
}

# Expected: 401 Unauthorized
```

### 4. Missing Channel/Conversation
```bash
# Try to create message without channelId or conversationId
POST http://localhost:8080/api/v1/messages
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "content": "No target specified"
}

# Expected: 400 Bad Request
```

## Performance Tests (Optional)

### Pagination Test
```bash
# Test pagination with different page sizes
GET http://localhost:8080/api/v1/messages/channel/1?page=0&size=50
GET http://localhost:8080/api/v1/messages/channel/1?page=1&size=50

# Verify page object contains: content[], totalElements, totalPages, etc.
```

### Search Performance
```bash
# Create 100+ messages and search
GET http://localhost:8080/api/v1/messages/search?query=hello&page=0&size=50

# Verify response time is reasonably fast (< 1s)
```

## Success Criteria

All tests should:
- ✅ Return HTTP status codes (201 for create, 200 for success, 400+ for errors)
- ✅ Include ApiResponse wrapper with code/message/data
- ✅ Include timestamps (createdAt, updatedAt)
- ✅ Validate input (non-blank names, size limits)
- ✅ Enforce authorization (owner-only operations)
- ✅ Support pagination (page, size, totalElements)
- ✅ Work with Swagger UI without modifications

## Swagger UI Testing

1. Open http://localhost:8080/api/v1/swagger-ui.html
2. Click "Authorize" button (top right)
3. Enter your Bearer token
4. Test all endpoints directly from Swagger UI
5. Response bodies should match examples above

## Common Issues & Solutions

### Issue: 401 Unauthorized
- **Cause**: Missing or invalid Bearer token
- **Solution**: Ensure token is from current login session

### Issue: 404 Not Found
- **Cause**: Resource ID doesn't exist (typo or already deleted)
- **Solution**: Double-check IDs from create response

### Issue: 400 Bad Request
- **Cause**: Validation failed (missing fields, invalid format)
- **Solution**: Check field requirements in error message

### Issue: 403 Forbidden
- **Cause**: User lacks required role (e.g., not team owner)
- **Solution**: Use admin account or team owner's token

## Next Phase (3) Features to Watch

- File upload/download endpoints
- Notification creation and delivery
- Advanced search with filters
- WebSocket real-time messaging
- Presence indicators
- Read receipts

---

**Last Updated**: May 13, 2026  
**Phase 2 Status**: ✅ Complete  
**Ready for**: Full integration testing

