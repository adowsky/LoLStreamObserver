import React from 'react';

export default class HeaderView extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="header">
                <img src={this.props.logoLocation} alt={this.props.logoAltName}/>
                <nav className="navigation">
                    {this.context.menu.map((obj) =>
                        <button key={obj.name} onClick={obj.action}>{obj.name}</button>)}
                </nav>
            </div>);
    }
}

HeaderView.contextTypes = {
    menu: React.PropTypes.array
};

HeaderView.defaultProps = {
    logoLocation: '/images/logo.png',
    logoAltName: 'LoLStreamObserver'
};