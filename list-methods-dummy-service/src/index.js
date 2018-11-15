const express = require('express')
const app = express()
const bodyParser = require('body-parser')

app.use(bodyParser.text({type: '*/*'}))

app.post('/', function (req, res, body) {
   console.log("Request received")
   res.set('Content-Type', 'text/xml')
   const response = req.body.replace("listMethods/", "listMethodsResponse/")
   res.send(response)
})

var server = app.listen(8081, function () {
   const host = server.address().address
   const port = server.address().port

   console.log("\"listMethods\" dummy app listening at http://%s:%s", host, port)
})
