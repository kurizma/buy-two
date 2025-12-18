What should happen (high level)
Frontend:

User picks an avatar file.

Call mediaService.uploadAvatar(file) (you already have this).

Get back MediaResponse with url.

Call auth/user-service endpoint to update the user profile’s avatar field with that URL.

Update currentUser in AuthService so the new avatar shows everywhere.​

Backend:

media-service already saves avatar as ownerType = USER and ownerId = userId.

user-service stores only the avatar URL string.

Optionally, when user changes avatar, you can delete the old one via media-service DELETE /media/images/{mediaId} later (same as product image delete).​

