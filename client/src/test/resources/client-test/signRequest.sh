#!/bin/bash
openssl ca -config openssl.cnf -in request.csr -out response.crt