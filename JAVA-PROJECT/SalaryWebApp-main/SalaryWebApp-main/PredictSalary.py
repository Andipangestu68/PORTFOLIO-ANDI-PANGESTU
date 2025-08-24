#!/usr/bin/env python
# coding: utf-8

# # Random Forest Regression

# In[2]:


import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import sys
import warnings


warnings.filterwarnings("ignore")

# ## Importing the dataset

# In[3]:

# arg 'Salary_Data.csv'
dataset_file = sys.argv[1]

# arg experience (independent var)
age = int(sys.argv[2])
gender = int(sys.argv[3])
education_level = int(sys.argv[4])
job_title = int(sys.argv[5])
years_of_experience = float(sys.argv[6])



dataset = pd.read_csv(dataset_file)


# In[4]:


# dataset.isnull().any()


# ## Preprocessing Data

# In[5]:


dataset = dataset.dropna()
# dataset


# In[6]:


from sklearn.preprocessing import LabelEncoder
label_encoder = LabelEncoder()
dataset['Gender'] = label_encoder.fit_transform(dataset['Gender'])
dataset['Education Level'] = label_encoder.fit_transform(dataset['Education Level'])
dataset['Job Title'] = label_encoder.fit_transform(dataset['Job Title'])
dataset


# In[19]:


X = np.array(dataset.iloc[:,:-1])
y = np.array(dataset.iloc[:,-1])
# X


# ## Split into train and test data

# In[20]:


from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=0 )


# ## Feature Scalling

# In[21]:


from sklearn.preprocessing import StandardScaler
sc = StandardScaler()
X_train = sc.fit_transform(X_train)
X_test = sc.transform(X_test)


# In[22]:


X_train


# ## Training the Random Forest Regression model on the Training set

# In[23]:


from sklearn.ensemble import RandomForestRegressor
rf = RandomForestRegressor(n_estimators=10, random_state=0)
rf.fit(X_train,y_train)


# ## Predicting the test set results

# In[24]:


# y_rf = rf.predict(X_test)
# np.set_printoptions(precision=2)
# print(np.concatenate((y_rf.reshape(len(y_rf), 1), y_test.reshape(len(y_test), 1)), 1))


# ## Evaluation

# In[25]:


# from sklearn.metrics import r2_score
#random forest
# r2_score(y_test, y_rf)


# In[26]:


print(rf.predict(sc.transform([[age,gender,education_level,job_title,years_of_experience]])).reshape(-1,1)[0][0])

