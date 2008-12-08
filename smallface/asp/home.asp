<%
Option Explicit
Response.Buffer = True
'response.write("Request form: " & request.form)

Dim dbConnection
Set dbConnection=Server.CreateObject("ADODB.Connection")
dbConnection.Open "Driver={SQL Server};Server=localhost;Database=perf_competition;Uid=dvg;Pwd=dvg;"
Dim vbCrLf
vbCrLf = "<br>"

Dim emailVal
Dim passwordVal
Dim loginVal
Dim registerVal
emailVal       = request.form("email")
passwordVal    = request.form("password")
loginVal       = request.form("login")
registerVal    = request.form("register")

Dim postVal
Dim postContentVal
Dim addFriendVal
Dim friendNameVal
postVal        = request.form("post")
postContentVal = request.form("postContent")
addFriendVal   = request.form("addFriend")
friendNameVal  = request.form("friendName")

Function GetLoggedInContent

    Dim content
    Dim rs
    Set rs = Server.CreateObject("ADODB.RecordSet")
    rs.Open "AuthenticateUserAndCountFriends '" & emailVal & "', '" & passwordVal & "'", dbConnection
    Dim friendCount
    Dim resultCount
    friendCount = -1   '-1 indicate no valid user
    resultCount = 0
    Do Until rs.EOF
        resultCount = resultCount + 1
        friendCount = rs("FriendCount")
        rs.MoveNext
    Loop
    rs.close
    Set rs = Nothing

    If resultCount <= 0 Then
        content = "Sorry " & emailVal & " either you are not registered, or your password is incorrect!"
    ElseIf resultCount > 1 Then
        content = "Sorry " & emailVal & " you are registered more than once which is a critical error. Please contact the administrator!"
    Else
        content = "Welcome " & emailVal & "!"
        content = content & vbCrLf & "You have " & friendCount & " friend(s)"

        Dim friendPosts
        friendPosts = "<table>"
        Set rs = Server.CreateObject("ADODB.RecordSet")
        rs.Open "GetFriendLimitedPosts 20, '" & emailVal & "'", dbConnection
        Do Until rs.EOF
            friendPosts = friendPosts & "<tr>" & _
                            "<td>" & rs("Time") & "</td>" & _
                            "<td>" & rs("Name") & "</td>" & _
                            "<td>" & rs("Post") & "</td>" & _
                            "</tr>"
            rs.MoveNext
        Loop
        rs.close
        Set rs = Nothing
        friendPosts = friendPosts & "</table>"

        content = content & vbCrLf & friendPosts

        content = content & vbCrLf & "<form method='POST' action='./home.asp'>" & _
                                     "<input name='email' type='hidden' value='"&emailVal&"'/><input name='password' type='hidden' value='"&passwordVal&"'/>" & _
                                     "<input name='postContent' type='text'/><input type='submit' name='post' value='New post'/></form>"
        content = content & vbCrLf & "<form method='POST' action='./home.asp'>" & _
                                     "<input name='email' type='hidden' value='"&emailVal&"'/><input name='password' type='hidden' value='"&passwordVal&"'/>" & _
                                     "<input name='friendName' type='text'/><input type='submit' name='addFriend' value='Add Friend'></form>"

        content = content & vbCrLf & "<form method='POST' action='./home.asp'>" & _
                                     "<input type='submit' name='logOut' value='Log Out'></form>"

    End If

    GetLoggedInContent = content
End Function


Dim content
Dim loggedIn
loggedIn = False
If loginVal <> "" Then
    ' log in
    content = GetLoggedInContent
ElseIf registerVal <> "" Then
    ' register a new user
    Dim createUser
    Set createUser = Server.CreateObject("ADODB.Command")
    Set createUser.ActiveConnection = dbConnection
    createUser.CommandText = "CreateUser '" & emailVal & "', '" & passwordVal & "'"
    'cmd.CommandType = adCmdStoredProc
    ' Ask the server about the parameters for the stored proc
    'cmd.Parameters.Refresh
    ' Assign a value to the 2nd parameter.
    ' Index of 0 represents first parameter.
    'cmd.Parameters("user") = emailVal
    'cmd.Parameters("password") = passwordVal
    createUser.Execute
    Set createUser = Nothing
    content = "<form method='POST' action='./home.asp'><table><tr><td>email:</td><td><input name='email' type='text'/></td></tr><tr><td>password:</td><td><input name='password' type='password'/></td></tr><tr><td colspan='2'><input type='submit' name='login' value='Login'/></td></tr><tr><td colspan='2'><input type='submit' name='register' value='Register'/></td></tr></table></form>"
ElseIf addFriendVal <> "" Then
    ' add a friend
    Dim addFriend
    Set addFriend = Server.CreateObject("ADODB.Command")
    Set addFriend.ActiveConnection = dbConnection
    addFriend.CommandText = "AddFriend '" & emailVal & "', '" & friendNameVal & "'"
    addFriend.Execute
    Set addFriend = Nothing
    content = GetLoggedInContent
ElseIf postVal <> "" Then
    ' post a message
    Dim addPost
    Set addPost = Server.CreateObject("ADODB.Command")
    Set addPost.ActiveConnection = dbConnection
    addPost.CommandText = "AddPost '" & emailVal & "', '" & postContentVal & "'"
    addPost.Execute
    Set addPost = Nothing
    content = GetLoggedInContent
Else
    ' first time page hit
    content = "<form method='POST' action='./home.asp'><table><tr><td>email:</td><td><input name='email' type='text'/></td></tr><tr><td>password:</td><td><input name='password' type='password'/></td></tr><tr><td colspan='2'><input type='submit' name='login' value='Login'/></td></tr><tr><td colspan='2'><input type='submit' name='register' value='Register'/></td></tr></table></form>"
End If


If content = "" Then
   content = "Something bad happened"
End If

dbConnection.Close
Set dbConnection = Nothing

%>
<html>
<body>
    <%=content%>
</body>
</html>