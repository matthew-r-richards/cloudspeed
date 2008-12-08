require 'rubygems'
require 'sequel'
# require 'memcache'

DB = Sequel.mysql('jitter_db', :user => 'jitter', :password => 'Jitter', :host => 'localhost')
# CACHE = MemCache.new 'localhost:11211', :namespace => 'jitter'

class Users < Sequel::Model(:users)
  set_schema do
    # You can't define a column and name it as the primary key in one go
    # unless it's the default
    varchar :email, :null => false
    primary_key :email, :auto_increment => false
    text :password, :null => false
  end
  one_to_many :posts
#  set_cache CACHE, :ttl => 3600

  # Assignment should update the underlying database
  def self.[]= (email, password)
    DB.transaction do
      if (email == nil || !(post = Users.find(:email => email)))
        user = Users.new
      end
      user.email = email
      user.password = password
      user.save
    end
  end
end

unless Users.table_exists?
  DB.transaction do
    puts "Creating table 'users'\n"
    Users.create_table
  end
end

class Friends < Sequel::Model(:friends)
  set_schema do
    # You can't define a column and name it as the primary key in one go
    # unless it's the default
    varchar :email, :null => false
    varchar :friend, :null => false
    primary_key :email, :friend, :auto_increment => false, :unique => true
  end
  one_to_many :users
#  set_cache CACHE, :ttl => 3600

  # Assignment should update the underlying database
  def self.[]= (email, friend)
    DB.transaction do
      if (email == nil || friend == nil)
        raise "email or friend is undefined"
      end
      if (email == friend)
        raise "can't be friends with yourself"
      end
      if (friendship = Friends.find(:email => email, :friend => friend))
        return friend
      end
      if !user = Users.find(:email => friend)
        raise "No such user: " + friend
      end
      friendship = Friends.new
      friendship.email = email
      friendship.friend = friend
      friendship.save
      friendship = Friends.new
      friendship.email = friend
      friendship.friend = email
      friendship.save
    end
    friend
  end
end

unless Friends.table_exists?
  DB.transaction do
    puts "Creating table 'friends'\n"
    Friends.create_table
  end
end

class Posts < Sequel::Model(:posts)
  set_schema do
    # You can't define a column and name it as the primary key in one go
    # unless it's the default
    primary_key :id
    timestamp :date, :null => false
    varchar :author, :null => false
    varchar :content, :null => false
  end
  many_to_one :users
#  set_cache CACHE, :ttl => 3600

  # Assignment should update the underlying database
  def self.[]=(id, values)
    DB.transaction do
      if (id == nil || !(post = Posts.find(:id => id)))
        post = Posts.new
        values[:date] = DateTime.now
      end
      post.date = values[:date]
      post.author = values[:author]
      post.content = values[:content]
      post.save
    end
  end
end

unless Posts.table_exists?
  DB.transaction do
    puts "Creating table 'posts'\n"
    Posts.create_table
  end
end
