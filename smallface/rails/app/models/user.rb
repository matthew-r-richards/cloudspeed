class User < ActiveRecord::Base
  has_many :friendships
  
  def add_friend friend_name
    friend = User.find_by_name friend_name
        
    return if friend.nil? || friend.id == id
    
    return if !(friendships.find_by_friend_id(friend.id)).nil?
    
    
    friendship = Friendship.new
    friendship.user_id = id
    friendship.friend_id = friend.id
    
    friendFriendship = Friendship.new
    friendFriendship.user_id = friend.id
    friendFriendship.friend_id = id
    friend.friendships << friendFriendship
    
    friendships << friendship
    
  end
  
end
