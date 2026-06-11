# Teams App Frontend Instructions

This repository contains the frontend for Teams App, a Microsoft Teams-like collaboration platform.

## Stack

* Angular
* TypeScript
* RxJS
* HTML/CSS
* REST API integration
* JWT authentication through existing interceptor

## Main Frontend Modules

* Authentication
* Home/dashboard
* Teams
* Channels
* Members
* Messages
* Attachments
* Notifications
* WebRTC/video calls
* User profile/settings

## General Rules

* Do not change the existing UI design unless explicitly requested.
* Do not change layout, colors, spacing, icons, or structure unless the task requires it.
* Do not rewrite unrelated files.
* Follow the existing Angular architecture, naming, and folder structure.
* Keep components focused on UI and state.
* Keep API calls inside services/fetchers.
* Use strong TypeScript interfaces.
* Do not use `any` unless there is no safe alternative.
* Prefer small, focused, safe changes.
* Do not remove existing functionality.
* Make sure frontend API paths match backend endpoints.

## Angular Code Rules

* Use typed interfaces for API responses, requests, and domain models.
* Keep HTTP logic inside fetcher/service classes.
* Keep components clean and readable.
* Handle loading, success, empty, and error states when relevant.
* Reuse existing services and patterns before creating new ones.
* Do not duplicate API logic inside components.
* Do not bypass the existing JWT interceptor unless absolutely necessary.
* Avoid unnecessary subscriptions that can cause memory leaks.
* Use RxJS patterns consistently with the existing project.

## Notification Frontend Rules

The frontend should consume backend notification APIs and show unread notifications cleanly without changing the current design.

Expected notification features:

* Fetch all notifications.
* Fetch unread notifications.
* Fetch notification by ID.
* Fetch unread notification count.
* Mark one notification as read.
* Mark all notifications as read.
* Delete notification.
* Refresh notification list after actions.
* Refresh unread count after actions.
* Display unread badge/count.
* Support polling if WebSocket/SSE notification delivery is not implemented yet.

Expected API methods in the notification fetcher/service:

* getNotifications()
* getUnreadNotifications()
* getNotificationById(id)
* getUnreadCount()
* markAsRead(id)
* markAllAsRead()
* deleteNotification(id)

Expected backend endpoints:

* GET /api/v1/notifications
* GET /api/v1/notifications/unread
* GET /api/v1/notifications/{id}
* GET /api/v1/notifications/unread/count
* POST /api/v1/notifications/{id}/read
* POST /api/v1/notifications/read-all
* DELETE /api/v1/notifications/{id}

Notification frontend behavior:

* Show notification bell badge based on unread count.
* Load unread notifications when notification panel opens.
* Refresh unread count after mark-as-read.
* Refresh unread count after mark-all-as-read.
* Refresh unread count after delete.
* Do not show notifications belonging to another user.
* Keep the current UI design unchanged.
* If real-time delivery is not implemented, use polling every 10–30 seconds.
* Design the service so WebSocket/SSE can be added later without rewriting the UI.

## WebRTC/Call Frontend Rules

* Keep WebRTC logic separate from normal notification fetching logic.
* Incoming video call events may need real-time behavior.
* Missed call notifications should still come from the notification API.
* Do not mix WebRTC signaling code directly into unrelated UI components.
* Keep call state clean and predictable.

## API Integration Rules

* Use the existing API config if available.
* Use the existing auth/JWT interceptor.
* Do not hardcode backend URLs if the project already has API configuration.
* Match request and response models with backend DTOs.
* Handle paginated responses correctly if backend returns pages.
* Do not assume response shape; inspect existing backend response wrappers.

## Testing Expectations

When changes are made, verify:

* Frontend builds successfully.
* No TypeScript errors.
* No broken imports.
* Notification service methods call correct endpoints.
* Unread count loads correctly.
* Notification list loads correctly.
* Mark-as-read updates count.
* Mark-all-as-read updates count.
* Delete updates count/list.
* Login token is attached through the existing interceptor.
* Existing UI design is preserved.

## Output Expectations

After making changes, provide:

* Summary of existing frontend notification implementation.
* Summary of what was missing.
* List of changed files.
* Explanation of frontend changes.
* Testing steps.
* Risks or assumptions.
* Suggested commit message.
