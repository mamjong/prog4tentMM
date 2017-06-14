// API version 1
var express = require('express');
var router = express.Router();
var moment = require('moment');
var jwt = require('jwt-simple');
var bcrypt = require('bcrypt');

var bodyParser = require('body-parser');
router.use(bodyParser.json()); // support json encoded bodies
router.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies

var path = require('path');
var pool = require('../db/db_connector');
var config = require('../config.json');

function encodeToken(username) {
    const payload = {
        exp: moment().add(10, 'days').unix(),
        iat: moment().unix(),
        sub: username
    };
    return jwt.encode(payload, config.secretkey);
}

function decodeToken(token, cb) {

    try {
        const payload = jwt.decode(token, config.secretkey);

        //check if token has expired
        const now = moment().unix();

        if (now > payload.exp) {
            console.log('Token has expired.')
        }

        //callback
        cb(null, payload);

    } catch (err) {
        //callback
        cb(err, null);
    }
}

router.post('/login', function(req, res) {

    var username = req.body.username || '';
    var password = req.body.password || '';

    if (username && password) {
        query_str = 'SELECT * FROM customer WHERE username = "' + username + '";';


        pool.getConnection(function (err, connection) {
            if (err) {
                throw err
            }
            connection.query(query_str, function (err, rows, fields) {
                connection.release();
                if (err) {
                    throw err
                }

                if (rows[0]) {
                    if (rows[0].hasOwnProperty('username') && rows[0].hasOwnProperty('password')) {
                        var hash = rows[0].password;
                        if (bcrypt.compareSync(password, hash)){
                            res.status(200).json(encodeToken(username));
                        } else {
                            res.json({error:"Invalid password"});
                        }
                    } else {
                        res.json({error: "Please enter a valid username and password"});
                    }
                } else {
                    res.json({error: "Please enter a valid username and password"});
                }

            });
        });
    } else {
        res.json({error: "Please enter a valid username and password"});
    }

});

router.post('/register', function(req, res, next){
    var username = req.body.username;
    var password = req.body.password;

    var hash = bcrypt.hashSync(password, 10);

    var query_str = {

        sql: 'INSERT INTO `customer`(username, password) VALUES (?,?)',
        values: [username, hash],

        timeout: 2000
    };

    pool.getConnection(function (err, connection) {
        if (err) {
            throw err
        }
        connection.query(query_str, function (err, rows, fields) {
            connection.release();
            if (err) {
                console.log(err);
            }
            res.status(200).json(rows);
        });
    });

});





router.get('*', function(request, response) {
    response.status(200);
    response.json({
        "description": "API V1"
    });
});

module.exports = router;
