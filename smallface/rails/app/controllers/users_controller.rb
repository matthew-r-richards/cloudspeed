class UsersController < ApplicationController
  # GET /users
  # GET /users.xml
  def index
    @users = User.find(:all)
    
    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @users }
    end
  end
  
  # GET /users/1
  # GET /users/1.xml
  def show
    @user = User.find(params[:id])
    
    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @user }
    end
  end
  
  # GET /users/new
  # GET /users/new.xml
  def new
    @user = User.new
    
    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @user }
    end
  end
  
  # GET /users/1/edit
  def edit
    @user = User.find(params[:id])
  end
  
  # POST /users
  # POST /users.xml
  def create
    tmp_user = User.new
    tmp_user.name = params[:name]
    tmp_user.password = params[:password]
    @user = User.find_by_name(tmp_user.name)
    if @user 
      if @user.password == tmp_user.password
        session[:user_id] = @user.id
        redirect_to('/home')        
      else 
        redirect_to('/login')
      end      
    else
      
      
      @user = tmp_user
      
      respond_to do |format|
        if @user.save
          flash[:notice] = 'User was successfully created.'
          format.html { redirect_to('/') }
          format.xml  { render :xml => @user, :status => :created, :location => @user }
        else
          format.html { render :action => "new" }
          format.xml  { render :xml => @user.errors, :status => :unprocessable_entity }
        end
      end
    end
  end
  

  def home
    @user = User.find(session[:user_id])
    
    if @user
      flash[:notice] = "Logged in #{@user.name}."      
    else
      session[:user_id] = nil
      redirect_to('/') 
    end
    
  end
  
  def addfriend
    @user = User.find(session[:user_id])
    
    @user.add_friend(params[:friend])
    
   
    flash[:notice] = "Tried to add a friend." 
    redirect_to('/home') 
   
    
  end
  
  def logout
    session[:user_id] = nil
    redirect_to('/')     
    
  end
  
  # PUT /users/1
  # PUT /users/1.xml
  def update
    @user = User.find(params[:id])
    
    respond_to do |format|
      if @user.update_attributes(params[:user])
        flash[:notice] = 'User was successfully updated.'
        format.html { redirect_to(@user) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @user.errors, :status => :unprocessable_entity }
      end
    end
  end
  
  # DELETE /users/1
  # DELETE /users/1.xml
  def destroy
    @user = User.find(params[:id])
    @user.destroy
    
    respond_to do |format|
      format.html { redirect_to(users_url) }
      format.xml  { head :ok }
    end
  end
end
