#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Wed Oct 19 18:52:42 2016

@author: grapefroot
"""
import warnings

import os
import sys 
import wave
import time
import numpy as np
from utils import *
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
import theano
import lasagne
import theano.tensor as T
from utils import train_net
from lasagne import layers
from lasagne import nonlinearities
from lasagne import init
import datetime


BP = "./musicdata/"

def get_path(i=1):
    return os.path.join(BP, 'spectrograms', 'spectrograms{}.pcl'.format(i))
    
num_chunks = 6

#X = np.concatenate([np.load(get_path(i)) for i in range(1, num_chunks + 1)])
X = np.load(get_path(1))
print X.shape
for i in range(2, num_chunks + 1):
	X = np.append(X, np.load(get_path(i)), axis=0)
print X.shape

with open(os.path.join(BP, "train_genres.txt"), "rt") as f_in:
    y = np.array(map(lambda x: x.strip(), f_in.readlines())[:2000*num_chunks])
    
genres = set(y)
num_classes = len(genres)
y = LabelEncoder().fit_transform(y)

def split_data():
    return train_test_split(X, y, train_size=0.8, test_size=0.2)

perm = np.random.permutation(len(y))
X, y = np.array(X)[perm].astype('float32'), np.array(y)[perm]
#Xreshape = X.reshape(X.shape[0], X.shape[1], X.shape[2])

X_train, X_valid, y_train, y_valid = split_data()
    
input_X, target_y = T.tensor3("X", dtype='float32'), T.vector("y", dtype='int32')
#nn = ??? Сделайте свою сеть, используя: Conv1DLayer + MaxPool1DLayer + DenseLayer

nn = {}

nn['input'] = layers.InputLayer(shape=(None, X.shape[1], X.shape[2]), input_var=input_X)
nn['conv1'] = layers.Conv1DLayer(nn['input'], num_filters=128, filter_size=4, stride=1)
nn['pool1'] = layers.MaxPool1DLayer(nn['conv1'], pool_size=4, stride=1)
nn['conv2'] = layers.Conv1DLayer(nn['pool1'], num_filters=256, filter_size=4, stride=1)
nn['pool2'] = layers.MaxPool1DLayer(nn['conv2'], pool_size=2, stride=1)
nn['conv3'] = layers.Conv1DLayer(nn['pool2'], num_filters=256, filter_size=4, stride=1)
nn['pool3'] = layers.MaxPool1DLayer(nn['conv3'], pool_size=2, stride=1)
nn['conv4'] = layers.Conv1DLayer(nn['pool3'], num_filters=512, filter_size=4, stride=1)
nn['pool4'] = layers.MaxPool1DLayer(nn['conv4'], pool_size=2, stride=1)


#fully-connected layers

nn['globalpooling'] = layers.GlobalPoolLayer(nn['pool4'])



nn['fc1'] = layers.DenseLayer(nn['globalpooling'], num_units=2048,
                              nonlinearity=nonlinearities.rectify,
                             W = init.GlorotUniform())
nn['fc2'] = layers.DenseLayer(nn['fc1'], num_units=2048, 
                              nonlinearity=nonlinearities.rectify,
                             W = init.GlorotUniform())

nn['output'] = layers.DenseLayer(nn['fc2'], num_units=num_classes,                                 
                                 nonlinearity=nonlinearities.softmax)

network = nn['output']

y_predicted = lasagne.layers.get_output(network)
all_weights = lasagne.layers.get_all_params(network)

loss = lasagne.objectives.categorical_crossentropy(y_predicted, target_y).mean()
accuracy = lasagne.objectives.categorical_accuracy(y_predicted, target_y).mean()
updates_sgd = lasagne.updates.adam(loss, all_weights)

train_fun = theano.function([input_X, target_y], [loss, accuracy], allow_input_downcast=True, updates=updates_sgd)
test_fun  = theano.function([input_X, target_y], [loss, accuracy], allow_input_downcast=True)

print 'starting training'

conv_nn = train_net(network, train_fun, test_fun, X_train, y_train, X_valid, y_valid, num_epochs=500, batch_size=300)

final_weights = lasagne.layers.get_all_params(network)

np.save('weights_{}'.format(datetime.datetime.now()), final_weights)


