from django.db import models

# Create your models here.

class User(models.Model):
	email = models.CharField(max_length=50, primary_key=True)
	password = models.CharField(max_length=50)
	friends = models.ManyToManyField('self')
	def __unicode__(self):
		return self.email
	
class Post(models.Model):
	date = models.DateTimeField('date published')
	content = models.CharField(max_length=256)
	author = models.ForeignKey(User)
	def __unicode__(self):
		return self.date.isoformat() + " " + self.author.email + " said " + self.content
