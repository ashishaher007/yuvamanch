package com.ymanch.helper;

public class CommonMessages {

	public final static String SUCCESS = "Success";
	public final static String FAILED = "Failed";
	public final String TEST_MESSAGE = "Test Api";
	public final static String STATUS = "status";
	public final static String MESSAGE = "message";
	public final String TOTAL_PAGES = "totalPages";
	public final static String TOTAL_ELEMENTS = "totalElements";
	public final String CURRENT_PAGE = "currentPage";
	public final String PAGE_SIZE = "pageSize";
	public String NEXT_PAGE_NO = "nextPageNo";
	public final String HAS_NEXT_PAGE = "hasNextPage";
	public final String USER_ROLE = "ROLE_USER";
	public final String ADMIN_ROLE = "ROLE_ADMIN";
	public final String SUPER_ADMIN_ROLE = "ROLE_SUPER_ADMIN";
	public final static String WALL_PAPER = "https://dev.strishakti.org/uploads/strishakti/posts/staticImageFolder/event-static-wall.jpg";
	public final static String METADATAURLPATTERN = "(https?://[\\w-]+(\\.[\\w-]+)+(:\\d+)?(/[^\\s]*)?)";
//	public final static String ANCHORTAGPATTERN ="<a\\s+href\\s*=\\s*\"?(https?://twitter\\.com/[\\w-]+)\"?[^>]*>[@\\w-]+</a>";

	/* User Message */
	public final String USER_DATA_NOT_FOUND = "Email Id not exist";
	public final String USER_REGISTARTION_SUCCESSFUL = "User registerd successfully";
	public final String USER_REGISTARTION_FAILED = "Email Id already exist";
	public final String USER_LOGIN_SUCCESSFUL = "User login successful";
	public final String USER_LOGIN_EMAIL_FAILED = "Please enter a valid email Id Or mobile No";
	public final String USER_LOGIN_MOBILE_NO_FAILED = "Please enter a valid mobile no";
	public final String USER_LOGIN_PASSWORD_FAILED = "Please enter a valid password";
	public final String USER_UPDATE_PASSWORD = "Password updated successfully";
	public final String USER_UPDATE_DETAILS = "User details update successfully";
	public final String USER_ADD_HOBBIES_SUCCESSFUL = "user hobbies added successfully";
	public final String USER_STATUS = "The user is In-Active";
	public final String USER_POST_TIMELINE = "Post not found";
	public final String USER_SEARCH_USER_NOT_FOUND = "User not found";
	public final String USER_DELETE_SUCCESSFUL = "User deleted successfully";
	public final String USER_DETAILS = "User details not found";
	public final String USER_GENDER_CONFLICT = "The user's gender must be set to female";
	public final String USER_ROLE_CONFLICT = "You can't add admin in the group";
	public final String USER_REGISTER_MOBILE_NO_FAILED = "This mobile number is already registered. Please use a different number or log in with the existing account";
	public final String ACTIVE_USER ="ACTIVE USERS FOUND SUCESSFULLY";
	/* End */

	/* Post Message */
	public final String POST_ADD_SUCCESSFULL = "uploaded successfully";
	public final String POST_DETAILS = "Post details not found";
	public final String POST_UPDATE_SUCCESSFUL = "updated successfully";
	public final String POST_DELETE_SUCCESSFUL = "deleted successfully";
	public final String POST_DELETE_FAILED = "Something went wrong while deleting the post";
	public final String POST_NOT_FOUND = "No posts found for the specified user";
	public final String POST_REACT_ADD_SUCCESSFUL = "React added to the post successfully";
	public final String POST_REACT_UPDATE_SUCCESSFUL = "React updated on the post successfully";
	public final String POST_REACT_MORE_THAN_ONE_MSG = "You have already reacted to this post";
	public final String POST_COMMENT_ADD_SUCCESSFUL = "Comment added to the post successfully";
	public final String POST_COMMENT_ON_COMMENT_ADD_SUCCESSFUL = "Comment added on comment successfully";
	public final String POST_COMMENT_DELETE_SUCCESSFUL = "Comment deleted successfully";
	public final String POST_UNAUTHORIZED_DELETE_FAILED = "Unauthorized to delete the comment";
	public final String POST_NO_MORE_POST = "You're all caught up! There are no more posts to show at the moment";
	public final String POST_REACT_NOT_FOUND = "You have no reaction on this post";
	public final String POST_REACT_REMOVED_SUCCESSFULL = "Your reaction has been successfully deleted";
	public final String POST_SAVED_SUCCESSFULL = "Your post has been successfully saved";
	public final String POST_ALREADY_SAVED = "This post has already been saved";
	public final String POST_SAVED_POST_DELETE_SUCCESSFULL = "Your saved post deleted successfully";
	public final String POST_OWN_TYPE_VALID = "Post owner type must be 'PUBLIC_EVENT' Or 'PRIVATE_EVENT'";
//	public final String POST_NO_MORE_POST_FROM_DISTRICT = "Advertisement already has been displayed";
	/* End */

	/* Friend Request */
	public final String FREQUEST_ALREADY_SENT = "Friend request already sent";
	public final String FREQUEST_ALREADY_FRIEND = "You are already a friend";
	public final String FREQUEST_SENT_SUCCESSFUL = "Friend request sent successfully";
	public final String FREQUEST_SENT_TO_OWN = "You can't send a friend request to yourself";
	public final String FREQUEST_ID_NOT_FOUND = "Friend request Id not found";
	public final String FREQUEST_APPROVED = "Friend request approved successfully";
	public final String FREQUEST_REJECT = "Friend request deleted successfully";
	public final String FREQUEST_NOT_FOUND = "Friend request not found";
	public final String FREQUEST_FRIEND_NOT_FOUND = "Friends not found";
	/* End */

	/* Home Page */
	public final String LACTIVITY_NOT_FOUND = "Last activities not found";
	/* End */

	/* Notification Message */
	public final String NOTIFICATION_SEND_FREQUEST = "You have a new friend request from ";
	public final String NOTIFICATION_ACCEPT_FREQUEST = " accepted your friend request.";
	public final String NOTIFICATION_ADD_COMMENT_POST = " responded to your post with a comment.";
	public final String NOTIFICATION_ADD_REACT_POST = " has reacted to your post.";
	public final String NOTIFICATION_NOT_FOUND = "No new notification";
	public final String NOTIFICATION_STATUS_UPDATE = "All notification updated successfully";
	/* End */

	/* Admin Message */
	public final String ADMIN_LOGIN_SUCCESSFUL = "Admin login successful";
	public final String ADMIN_ACTIVE_USER = "Failed to retrieve active user count";
	/* END */

	/* Forget Password */
	public final String FP_EMAIL_SEND_SUCCESSFULL = "Forget password link sent to your email Id";
	public final String FP_INVALID_OTP = "Invalid Otp";
	public final String FP_OTP_NOT_FOUND = "Otp not found";
	public final String FP_PASS_UPDATE_SUCCESSFUL = "Password updated successfully";
	/* End */

	/* Story */
	public final String S_STORY_ADD_SUCCESSFULL = "The story was successfully added to the profile";
	public final String S_STORY_NOT_PRESENT = "There are no stories available at this time";
	public final String S_STORY_DELETE_SUCCESSFUL = "Story removed successfully";
	public final String S_STORY_DELETE_FAILED = "An error occurred while deleting the story";
	public final String S_STORY_NOT_FOUND = "Stories not found";
	public final String S_STORY_VIEWED_SUCCESSFUL = "Story has been viewed successfully";
	public final String S_STORY_VIEWED_ALREADY = "You've already viewed this story";
	/* END */

	/* Group */
	public final String GROUP_CREATED_SUCCESSFUL = "Group created successfully";
	public final String GROUP_USER_ALREADY_IN_GROUP = "User is already a member of the group";
	public final String GROUP_USER_ADDED_SUCCESSFUL = "User added to the group successfully";
	public final String GROUP_NO_GROUPS = "No groups found for the user";
	public final String GROUP_DELETE_SUCCESSFUL = "Group deleted successfully";
	public final String GROUP_ADMIN_NOT_PRESENT = "Admin for the group is not available";
	public final String GROUP_DETAILS_UPDATE_SUCCESSFUL = "Group details have been successfully updated";
	public final String GROUP_USER_REMOVED_FROM_GROUP = "User has been successfully removed from the group";

	/* END */

	/* Group */
	public final String PAGE_CREATED_SUCCESSFUL = "Page created successfully";
	public final String PAGE_NO_PAGES = "No pages available for the user";
	public final String PAGE_NAME_ALREADY_EXIT = "A page with this name already exist";
	public final String PAGE_USER_ALREADY_FOLLOW_PAGE = "You are already following this page. Thanks for being part of the community";
	public final String PAGE_USER_FOLLOW_PAGE_SUCCESSFUL = "You are now following this page. Welcome to the community";
	public final String PAGE_DETAILS_UPDATE_SUCCESSFUL = "Page details have been successfully updated";
	public final String PAGE_DELETE_SUCCESSFUL = "Page deleted successfully";
	public final String PAGE_USER_REMOVED_FROM_PAGE = "User has been successfully removed from the page";
	/* END */

	/* Group */
	public final String DISPUTE_DTITLE_ALREADY_PRESENT = "Dispute title already present";
	public final String DISPUTE_DTITLE_ADD_SUCCESSFUL = "Dispute title added successfully";
	public final String DISPUTE_DTITLE_NOT_FOUND = "Dispute titles not found";
	public final String DISPUTE_ALREADY_RAISED = "A dispute for this issue has already been raised";
	public final String DISPUTE_RAISED_SUCCESSFUL = "Your dispute has been raised successfully";
	/* END */

	/* Events */
	public final String EVENT_CAT_ADD_SUCCESSFUL = "Event category added successfully";
	public final String EVENT_CREATE_SUCCESSFUL = "Event created successfully";
	public final String EVENT_DELETE_SUCCESSFUL = "Event deleted successfully";
	public final String EVENT_UPDATE_SUCCESSFUL = "Event details updated successfully";
	public final String EVENT_USER_ATTEMPT_TO_PARTICIPANT_OWN_EVENT = "You can't participant in you own auction";
	public final String EVENT_ADD_PARTICIPANT_SUCCESSFUL = "Congratulations! You have successfully registered for the event";
	public final String PARTICIPANT_ALREADY_PARTICIPATE = "You already registered for the event";
	public final static String PARTICIPANT_ADMIN_REMOVED_SUCCESSFUL = "Participant successfully removed by host admin from event";
	public final static String PARTICIPANT_REMOVED_SUCCESSFUL = "User successfully exited the event";
	/* END */

	/* Post Insight */
	public final static String POST_INSIGHT_ADD_SUCCESSFUL = "Post reach/view added successfully";
	/* END */

	/* Boost */
	public final String Boost_CREATED_SUCCESSFUL = "Post Boosted successfully";
	public final String POST_ALREADY_BOOSTED = "Post Boosted already";
	public final static String BOOST_POST_NOT_FOUND = "There are no more boosted posts available at the moment";
//	/* END */
	
	/* Birthday */
	public final static String Birthday_notification = "'s Birthday on ";
//	/* END */

	/* Organisation */
	public final static String ORG_ADD_SUCCESSFUL = "Organisation added successfully";
	/* END */
	
	/*Hashtag*/
	public final static String Hashtag_NOT_FOUND = "Hashtag not found";
}
