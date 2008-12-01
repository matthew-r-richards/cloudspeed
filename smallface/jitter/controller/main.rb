# Default url mappings are:
#  a controller called Main is mapped on the root of the site: /
#  a controller called Something is mapped on: /something
# If you want to override this, add a line like this inside the class
#  map '/otherurl'
# this will force the controller to be mounted on: /otherurl

class MainController < Ramaze::Controller
  layout '/page'

  # the index action is called automatically when no other action is specified
  def index
    @title = "Welcome to Jitter"
    if request.request_method == 'POST'
      if email = request['email']
        email.strip!
        if email.empty?
          failed("Please enter an email address")
        end
        if password = request['password']
          password.strip!
          if password.empty?
            failed("Please enter a password")
          end
        end
        if reg = request['register']
          register(email, password)
        else
          login(email, password)
        end
      end
    end
  end

  # the home action is the main UI, listing the posts by self and other users
  def home
    if logout = request['logout']
      redirect Rs('index')
    end
    @title = "Jitter Home"
    @user = flash[:user]
    if request.request_method == 'POST'
      if content = request['postContent']
        content.strip!
        if !content.empty?
          Posts[nil] = {:author => @user, :content => content}
        end
      end
      if friend = request['friendName']
        friend.strip!
        if !friend.empty?
          begin
            Friends[@user] = friend
          rescue RuntimeError => error
            failed(error.message)
            redirect Rs('home')
          end
        end
      end
    end
    @user_friends = Friends.dataset.filter(:email => @user)
    @user_friendcount = (@user_friends) ? @user_friends.size : 0
    @posts = []
    authors = [ @user ]    # include my own posts
    @user_friends.each do |friend|
      authors << friend[:friend]
    end
    authors.each do |author|
      Posts.dataset.filter(:author => author).each do |post|
        date = post[:date]
        content = post[:content]
        @posts << [date, author, content]
      end
    end
    @posts.sort! {|a,b| b[0] <=> a[0]} # reverse sort by date
    @posts.slice!(20..-1) # limit number of posts shown
  end

  helper :aspect
  after( :home ) {
      flash[:user] = @user # keep the logged-in session going
  }

  def register (email, password)
    if Users.find(:email => email)
      failed("User '#{email}' already exists")
    else
      Users[email] = password
      flash[:error] = 'You may now log in.'
    end
  end

  def login (email, password)
    user = Users.find(:email => email)
    if (!user) || (user[:password] != password)
      failed("Email or password is incorrect")
    else
      flash[:user] = email
      redirect Rs('home')
    end
  end
  
  # the string returned at the end of the function is used as the html body
  # if there is no template for the action. if there is a template, the string
  # is silently ignored
  def notemplate
    "there is no 'notemplate.xhtml' associated with this action"
  end

  def failed (message)
    flash[:error] = message
  end

end
