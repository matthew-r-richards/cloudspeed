from django.http import HttpResponse, HttpResponseRedirect
from smallface.smallfaceapp.models import User, Post
from datetime import datetime

loginpage = '<html>  <body>     <form method="POST">       <table>       <tr><td>email:</td><td><input name="email" type="text"/></td></tr>       <tr><td>password:</td><td><input name="password" type="password"/></td></tr>       <tr><td colspan="2"><input type="submit" name="login" value="Login"/></td></tr>       <tr><td colspan="2"><input type="submit" name="register" value="Register"/></td></tr>       </table>     </form>  </body></html>'

def login(request):
	if request.method == 'POST':
		email = request.POST["email"]
		password = request.POST["password"]
		if "login" in request.POST:
			# TODO check if the user exists
			u = User.objects.get(email=email)
			if u.password == password:
				# put user in session
				request.session["user"] = u
				# forward to home
				return HttpResponseRedirect('/smallface/home')
			else:
				# write error
				return HttpResponse("Wrong password!" + loginpage)
		elif "register" in request.POST:
			u = User(email=email, password=password)
			u.save()
			return HttpResponse(loginpage)
	else:
		return HttpResponse(loginpage)
	
def home(request):
	if "user" in request.session:
		u = request.session["user"]
		if "logout" in request.POST:
			del request.session["user"]
			return HttpResponseRedirect('/smallface/login')
		elif "addFriend" in request.POST:
			friend = request.POST["friendName"]
			u.friends.add(friend)
			return homepage(u)
		elif "post" in request.POST:
			p = Post(date = datetime.now(), content = request.POST["postContent"], author = u)
			p.save()
			return homepage(u)
		else:
			return HttpResponse(homepage(u))
	else:
		return HttpResponseRedirect('/smallface/login')


def homepage(user):
	content = """
<html> <body>
	Wellcome """ + user.email + """
	<br/> You have """ + str(user.friends.count()) + """ friends<br/>
	<table> 
		""" + print_posts(user) + """
	</table>  
	<form method="POST">
		<input type="text" name="postContent"/>
		<input type="submit" name="post" value="New Post">  
	</form>
	<form method="POST">
		<input type="text" name="friendName"/>
		<input type="submit" name="addFriend" value="Add Friend">  
	</form>  
	<form method="POST">
		<input type="submit" name="logout" value="Logout">
	</form>  
</body></html>
"""
	return HttpResponse(content)

def print_posts(user):
	posts = []
	for p in Post.objects.filter(author = user).order_by("date").reverse()[0:20]:
		posts.append((p.date, p))
	for friend in user.friends.all():
		friendsPosts = Post.objects.filter(author = friend).order_by("date").reverse()[0:20]
		for p in friendsPosts:
			posts.append((p.date, p))
	posts.sort()
	posts.reverse()
	posts = posts[0:20]
	content = ""
	for (d, p) in posts:
		content += "<tr><td>" + str(p.date) + " " + p.author.email + " said " + p.content + "</td></tr>\n"
	return content
		

	
	