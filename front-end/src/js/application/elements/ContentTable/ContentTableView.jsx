import React from 'react';

export default class ContentTableView extends React.Component{

    constructor(props){
        super(props);
    }

    render(){
        return <div className="content">
            {this.props.children}
        </div>
    }
}
