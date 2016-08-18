import React from 'react';
import Header from './elements/header/Header.jsx'
import ContentTable from './elements/ContentTable/ContentTable.jsx'

export default class AppView extends React.Component {
   constructor(props) {
        super(props)
    }

    render() {
        return (
                <div>
                    <Header />
                    <ContentTable />
                    {this.props.children}
                </div>
        );
    }
}
