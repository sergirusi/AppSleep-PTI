var express = require("express");
var app = express();
var fs = require("fs");
var MongoClient = require('mongodb').MongoClient;
var assert = require('assert');
var url = 'mongodb://localhost:27017/json';
var ObjectId = require('mongodb').ObjectID;
var mongodb = require("mongodb");

var db;

app.all('*', function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "X-Requested-With");
    next();
});

var bodyParser = require('body-parser');


app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

function existsUser(useri, exist) {
	console.log("existeix ", useri);
	db.collection('users').find({user:useri}).toArray(function(err, results){
		console.log(results);
	});
}

//select de le bases de dades
app.get('/getusers', function(req, res) {
	var cursor = db.collection('users').find();
	cursor.each(function(err, doc) {
		assert.equal(err, null);
		if(doc!=null) {
			res.status(201).json(doc);
		}
		else {
			res.sendStatus("error");
		}
	});		
	
});

//encendre la camera des del android
app.post('/camon', function(req, res) {
	var us = req.body.user;
	db.collection('users').update({"user": us}, {$set:{"cam": "true"}}, function(err, doc) {
                if(err) {
                        console.log("Error");
                        res.send("Error al indexar");
                } else {
                        console.log("introduit");
			res.sendStatus(201);
                }
     	});
});

//apagar la camera des del android
app.post('/camoff', function(req, res) {
        var us = req.body.user;
        db.collection('users').update({"user": us}, {$set:{"cam": "false"}}, function(err, doc) {
                if(err) {
                        console.log("Error");
                        res.send("Error al indexar");
                } else {
                        console.log("desintroduit");
			 res.sendStatus(201);
                }
        });
});


//get del usuari
app.get('/getuser/:user', function(req, res) {
	var query = {'user': req.params.user};
	db.collection('users').findOne(query, function(err, results){
		    res.send(results);
    	});
});

//get del usuari
app.get('/cam/:user', function(req, res) {
	var query = {'user': req.params.user};
	db.collection('users').findOne(query, function(err, results){
		    if(results != null) {
		        res.send(results.cam);
		    }
		    res.sendStatus(404);
    	});
	//var obj = JSON.parse(cursor);
	//console.log(obj);
});

app.get('/getgrafiques/:user', function(req, res) {
    var query = {'user': req.params.user};
    db.collection('users').findOne(query, function(err, results) {
        console.log(results);
        if (results != null) {
            if(results.graf == 0) {
                var result = {"graf": [0.0]};
                res.send(result);
            }
            else {
                var id = results.graf;
                db.collection('grafs').findOne({"_id": new ObjectId(id)}, function(err, results) {
                    console.log(results);
                    res.send(results);
                 });
            }
        }
        else res.send(404);
    });
});


function getgraf(user, cb) {
    var query = {'user': user}; 
    console.log(query);
    db.collection('users').findOne(query, function(err, results) {
        console.log(results);
        if (results != null) {
            if(results.graf == 0) {
                cb(0);
            }
            else {
                var id = require('mongodb').ObjectID(results.graf);
                db.collection('grafs').findOne({"_id": id}, function(err, results) {
                    cb(results);
                 });
            }
        }
    });

}

//raspberry post user, json 
app.post('/data', function(req, res) {
	var us = req.body.user;
	var graf = req.body;
	var key = "user";
	delete graf[key];
	var fArray = [];
	getgraf(us, function(id) {
	    if(id == 0) {
	        fArray.push(graf.graf);
	        var arrayjson = JSON.parse(JSON.stringify(fArray));
	        var query = {"graf": arrayjson};
            db.collection('grafs').insertOne(query, function(err, doc) {
                if (err) {
                    console.log("Failed to create new graf.");
                    res.send("Falide to creat new graf.");
                } else {
                    db.collection('users').update({"user": us}, {$set:{"graf": doc.insertedId}}, function(err, doc) {
                        if(err) {
                            console.log("Error");
                            res.sendStatus(404);
                        } else {
                            res.sendStatus(201);
                            console.log("indexat");
                        }
                    });
                }
            });

	    }
	    else {
            fArray = id.graf;
            fArray.push(graf.graf);
            var arrayjson = JSON.parse(JSON.stringify(fArray));
            var idf = require('mongodb').ObjectID(id._id);
            db.collection('grafs').update({"_id": idf}, {$set: {"graf": arrayjson}}, function(err, doc) {
                if(err) {
                    console.log("Error");
                    res.sendStatus(404);
                } else {
                    res.sendStatus(201);
                    console.log("indexat");
                }
            });
        }
	});
});

app.post('/temps', function(req,res) {
    var query = {'user': req.body.user};
console.log(query);
    var query1 = {'temps': req.body.temps};
	console.log(query1);
    db.collection('users').update(query, {$set:query1}, function(err, doc) {
        if(err) {
            console.log("Error");
            res.sendStatus(404);
        } else {
            console.log("changed");
            res.sendStatus(201);
        }
    });
});


app.post('/reset_graf', function(req,res) {
    var us = req.body.user;
    db.collection('users').update({"user": us}, {$set:{"graf": 0}}, function(err, doc) {
        if(err) {
            console.log("Error");
            res.sendStatus(404);
        } else {
            console.log("changed");
            res.sendStatus(201);
        }
    });
});

//android post user
app.post('/create_user', function(req, res) {
	var data = req.body;
	var user = JSON.stringify(req.body.user);
	console.log(data); 
	db.collection('users').insertOne(data, function(err, doc) {
	if (err) {
	      console.log("Failed to create new contact.");
	      res.send(err);
        } else {
	      res.sendStatus(200);
        }
        });
});

app.post('/user_exist', function(req, res) {
        var data = req.body;
        var user = JSON.stringify(req.body.user);
	console.log(data);
        db.collection('users').insertOne(data, function(err, doc) {
        if (err) {
              console.log("Failed to create new contact.");
              res.send(err);
        } else {
              res.sendStatus(200);
              db.collection('users').remove(data, function(err,result) {
	});
        }
        });
});


// Create a database variable outside of the database connection callback to reuse the connection pool in your app.
var db;


// Connect to the database before starting the application server.
mongodb.MongoClient.connect(url, function (err, database) {
  if (err) {
    console.log(err);
    process.exit(1);
  }

  // Save database object from the callback for reuse.
  db = database;
  console.log("Database connection ready");
});

var server = app.listen(process.env.PORT || 8080, function () {
    var port = server.address().port;
    console.log("App now running on port", port);
  });
