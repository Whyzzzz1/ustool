import request from './axios';

export function getstruAnalysis(data) {
    return request({
      url: '/struAnalysis',
      method: 'post',
      data
    })
  }

  export function dependencyParsing(data) {
    return request({
      url: '/dependencyParsing',
      method: 'post',
      data
    })
  }
  export function getparse(data) {
    return request({
      url: '/parse',
      method: 'post',
      data
    })
  }

    export function getsemparse(data) {
    return request({
      url: '/predict',
      method: 'post',
      data
    })
  }
  
  
  